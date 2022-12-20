package com.lfl.advent2022.days;

import com.lfl.advent2022.LinesConsumer;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.primitive.IntLists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.collector.Collectors2;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.lfl.advent2022.days.Day19.Mineral.GEODE;
import static java.lang.Integer.parseInt;

@Slf4j
@Service
public class Day19 implements LinesConsumer {

    private static final Pattern pattern = Pattern.compile("Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.");

    @Override
    public void consume(List<String> lines) {
        MutableList<Blueprint> blueprints = lines.stream()
                .map(Blueprint::of)
                .collect(Collectors2.toList());

        log.info("blueprints = {}", blueprints.size());

        blueprints.stream()
                .parallel()
                .map(blueprint -> BlueprintRunner.of(24, blueprint))
                .forEach(runner -> log.info("id = {}, geode = {}", runner.getBlueprint().getId(), runner.cacheRun()));
        int sum = blueprints.stream()
                .mapToInt(blueprint -> maxByBlueprint.get(blueprint) * blueprint.getId())
                .sum();

        log.info("sum = {}", sum);

//        maxByBlueprint.clear();
//
//        blueprints.stream()
//                .limit(3)
//                .parallel()
//                .map(blueprint -> BlueprintRunner.of(32, blueprint))
//                .forEach(runner -> log.info("id = {}, geode = {}", runner.getBlueprint().getId(), runner.cacheRun()));
//        int product = blueprints.stream()
//                .limit(3)
//                .mapToInt(maxByBlueprint::get)
//                .reduce(1, (a, b) -> a * b);
//
//        log.info("product = {}", product);
    }

    private static final Map<Blueprint, Integer> maxByBlueprint = new ConcurrentHashMap<>(30);

    @Data
    @Builder
    @EqualsAndHashCode(exclude = "createdRobot")
    private static class BlueprintRunner {
        private int minutesRemaining;
        private Blueprint blueprint;
        private MutableIntList robots;
        private MutableIntList resources;

        private Mineral createdRobot;

        public static BlueprintRunner of(int minutesRemaining, Blueprint blueprint) {
            return BlueprintRunner.builder()
                    .minutesRemaining(minutesRemaining)
                    .blueprint(blueprint)
                    .robots(IntLists.mutable.of(1, 0, 0, 0))
                    .resources(IntLists.mutable.of(0, 0, 0, 0))
                    .build();
        }

        public int cacheRun() {
            int run = this.run();
            maxByBlueprint.merge(this.getBlueprint(), run, Integer::max);
            return run;
        }

        public int run() {
            //   log.info("runner = {}", this);
            int max = maxByBlueprint.getOrDefault(blueprint, 0);
            int upperLimit = getRobotNumber(GEODE) * minutesRemaining + geodes()
                    + (minutesRemaining * minutesRemaining - minutesRemaining) / 2;//don’t factorize due to integer division

            if (upperLimit < max) {
                return 0;
            }

            if (isTimeUp()) {
                return geodes();
            }

            collect();

//            if (canCreateRobot(Mineral.GEODE)) {
//                return BlueprintRunner.builder().minutesRemaining(minutesRemaining - 1)
//                        .blueprint(blueprint)
//                        .robots(IntLists.mutable.ofAll(robots))
//                        .resources(IntLists.mutable.ofAll(resources))
//                        .build()
//                        .createRobot(Mineral.GEODE)
//                        .cacheRun();
//            }

            MutableList<BlueprintRunner> runs = Arrays.stream(Mineral.values())
                    .filter(this::canCreateRobot)
                    .filter(this::isUsefulToCreate)
                    .filter(this::isUsefulToCreate2)
                    .map(robotType -> BlueprintRunner.builder()
                            .minutesRemaining(minutesRemaining - 1)
                            .blueprint(blueprint)
                            .robots(IntLists.mutable.ofAll(robots))
                            .resources(IntLists.mutable.ofAll(resources))
                            .build()
                            .createRobot(robotType))
                    .collect(Collectors2.toList());
            MutableList<BlueprintRunner> runners = runs.with(BlueprintRunner.builder()
                    .minutesRemaining(minutesRemaining - 1)
                    .blueprint(blueprint)
                    .robots(IntLists.mutable.ofAll(robots))
                    .resources(IntLists.mutable.ofAll(resources))
                    .build());
            //log.info(this + "");
            return runners.stream().mapToInt(BlueprintRunner::cacheRun).max().orElse(0);
        }

        private void collect() {
            Arrays.stream(Mineral.values())
                    .forEach(type -> addResources(type, getRobotNumber(type)));
            if (createdRobot != null) {
                addResources(createdRobot, -1);
                createdRobot = null;
            }
        }

        private boolean isUsefulToCreate(Mineral robotType) {
            return robotType == GEODE || blueprint.maxFor(robotType) > getResourceQuantity(robotType) - 2;
        }

        private boolean isUsefulToCreate2(Mineral robotType) {
            return robotType == GEODE || blueprint.maxFor(robotType) > getRobotNumber(robotType);
        }

        private BlueprintRunner createRobot(Mineral robotType) {
            if (canCreateRobot(robotType)) {
                Map<Mineral, Integer> cost = blueprint.getCosts().get(robotType);
                cost.forEach((mineralType, price) -> addResources(mineralType, -price));
                addRobot(robotType);
                createdRobot = robotType;
            }

            return this;
        }

        private boolean canCreateRobot(Mineral robotType) {
            Map<Mineral, Integer> cost = blueprint.getCosts().get(robotType);

            return cost.entrySet()
                    .stream()
                    .allMatch(entry -> getResourceQuantity(entry.getKey()) >= entry.getValue());
        }

        private int addResources(Mineral type, int delta) {
            return resources.set(type.ordinal(), getResourceQuantity(type) + delta);
        }

        private int getResourceQuantity(Mineral type) {
            return resources.get(type.ordinal());
        }

        private void addRobot(Mineral robotType) {
            robots.set(robotType.ordinal(), getRobotNumber(robotType) + 1);
        }

        private int getRobotNumber(Mineral robotType) {
            return robots.get(robotType.ordinal());
        }

        private boolean isTimeUp() {
            return minutesRemaining == 0;
        }

        public int geodes() {
            return getResourceQuantity(GEODE);
        }
    }

    @Builder
    @Data
    @EqualsAndHashCode(of = "id")
    private static class Blueprint {
        private int id;
        private Map<Mineral, ? extends Map<Mineral, Integer>> costs;
        private Map<Mineral, Integer> maxFor;

        public static Blueprint of(String line) {
            Matcher matcher = pattern.matcher(line);
            matcher.find();

            int id = parseInt(matcher.group(1));
            MutableMap<Mineral, MutableMap<Mineral, Integer>> costs = Maps.mutable
                    .of(Mineral.ORE, Maps.mutable.of(Mineral.ORE, parseInt(matcher.group(2))))
                    .withKeyValue(Mineral.CLAY, Maps.mutable.of(Mineral.ORE, parseInt(matcher.group(3))))
                    .withKeyValue(Mineral.OBSIDIAN, Maps.mutable.of(Mineral.ORE, parseInt(matcher.group(4)))
                            .withKeyValue(Mineral.CLAY, parseInt(matcher.group(5))))
                    .withKeyValue(GEODE, Maps.mutable.of(Mineral.ORE, parseInt(matcher.group(6)))
                            .withKeyValue(Mineral.OBSIDIAN, parseInt(matcher.group(7))));

            MutableMap<Mineral, Integer> maxes = Arrays.stream(Mineral.values())
                    .collect(Collectors2.toMap(
                            type -> type,
                            type -> maxFor(type, costs)
                    ));

            return Blueprint.builder().id(id).costs(costs).maxFor(maxes).build();
        }

        public int maxFor(Mineral type) {
            return maxFor.get(type);
        }

        public static int maxFor(Mineral type, Map<Mineral, ? extends Map<Mineral, Integer>> costs) {
            return costs.values().stream()
                    .flatMap(map -> map.entrySet().stream())
                    .filter(entry -> entry.getKey() == type)
                    .mapToInt(Map.Entry::getValue)
                    .max()
                    .orElse(0);
        }

        @Override
        public String toString() {
            return "Blueprint(" +
                    "id=" + id +
                    ')';
        }
    }

    enum Mineral {
        ORE,
        CLAY,
        OBSIDIAN,
        GEODE;
    }
}
