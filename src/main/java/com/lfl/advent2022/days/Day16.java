package com.lfl.advent2022.days;

import com.lfl.advent2022.LinesConsumer;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.Triple;
import org.eclipse.collections.impl.collector.Collectors2;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.tuple.Tuples;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;


@Slf4j
@Service
public class Day16 implements LinesConsumer {

    private static final Pattern pattern = Pattern.compile("Valve (?<id>[A-Z]+) has flow rate=(?<rate>\\d+); tunnel(s?) lead(s?) to valve(s?) (?<ids>.+)");

    private static final MutableMap<String, Valve> valves = Maps.mutable.empty();

    private ImmutableList<Valve> importants;

    @Override
    public void consume(List<String> lines) {
        lines.forEach(Valve::of);

        importants = valves.select(valve -> valve.getRate() > 0).selectUnique().toImmutableList();

        //long score = findBestScore(importants, 30);
        //log.info("score = {}", score);

        long max = Interval.fromToExclusive(importants.size() / 2, 0)
                .by(-1)
                .collectLong(i -> findBestScoreForBothWith(i, 26))
                .max();
        log.info("max = {}", max);
    }

    private long findBestScoreForBothWith(int i, int maxMinute) {
        Iterator<int[]> iterator = CombinatoricsUtils.combinationsIterator(importants.size(), i);
        long total = CombinatoricsUtils.binomialCoefficient(importants.size(), i);
        Iterable<int[]> iterable = () -> iterator;
        log.info("i = {}", i);
        AtomicLong current = new AtomicLong();
        return StreamSupport.stream(iterable.spliterator(), false)
                .peek(indexes -> log.info("{}/{}", current.incrementAndGet(), total))
                .mapToLong(indexes -> findBestScoreForBoth(indexes, maxMinute))
                .max()
                .getAsLong();
    }

    private long findBestScoreForBoth(int[] indexes, int maxMinute) {
        //log.info("In for i = {}", indexes.length);
        ImmutableList<Valve> importantsForElephant = importants.selectWithIndex((valve, index) -> Arrays.binarySearch(indexes, index) >= 0);
        ImmutableList<Valve> importantsForHuman = importants.selectWithIndex((valve, index) -> !(Arrays.binarySearch(indexes, index) >= 0));

        return findBestScore(importantsForElephant, maxMinute) + findBestScore(importantsForHuman, maxMinute);
    }

    private final Map<Triple<Valve, Valve, ImmutableSet<Valve>>, Optional<ImmutableList<Valve>>> cache = Maps.mutable.empty();

    private Optional<ImmutableList<Valve>> cachedFindPath(Valve start, Valve end, ImmutableSet<Valve> visited) {
        Triple<Valve, Valve, ImmutableSet<Valve>> triple = Tuples.triple(start, end, visited);
        return cache.computeIfAbsent(triple, key -> findPath(key.getOne(), key.getTwo(), key.getThree()));
    }

    private Optional<ImmutableList<Valve>> findPath(Valve start, Valve end, ImmutableSet<Valve> visited) {
        if (start.equals(end)) {
            return Optional.of(Lists.immutable.of(start));
        }

        ImmutableSet<Valve> newWith = visited.newWith(start);

        return start.getChildren()
                .reject(visited::contains)
                .collect(child -> cachedFindPath(child, end, newWith))
                .reject(Optional::isEmpty)
                .collect(Optional::get)
                .minByOptional(RichIterable::size)
                .map(nextPath -> Lists.immutable.of(start).newWithAll(nextPath));
    }

    private long findBestScore(ImmutableList<Valve> importants, int maxMinute) {
        Valve start = valves.get("AA");
        Path path = importants.collect(important -> visit(start, important, Path.of(Lists.immutable.empty(), Lists.immutable.empty()), importants, maxMinute))
                //.tap(list -> log.info("score = {}, path = {}", list.score(maxMinute), list))
                .maxBy(path1 -> path1.score(maxMinute));
        //log.info("path = {}", path);
        return path.score(maxMinute);
    }

    private Path visit(Valve start, Valve important, Path previousPath, ImmutableList<Valve> importants, int maxMinute) {
        if (previousPath.contains(important)) {
            return previousPath;
        }

        Optional<ImmutableList<Valve>> optPath = cachedFindPath(start, important, Sets.immutable.empty());
        if (optPath.isEmpty()) {
            return previousPath;
        }

        ImmutableList<Valve> path = optPath.get();
        Path newPath = Path.of(previousPath.path().newWithAll(previousPath.path().getLastOptional().filter(valve -> valve.equals(start)).isEmpty() ? path : path.subList(1, path.size())),
                previousPath.orderOfOpening().newWith(important));

        if (newPath.exceedsLimit(maxMinute)) {
            return previousPath;
        }

        return importants.reject(previousPath::contains)
                .collect(valve -> visit(important, valve, newPath, importants, maxMinute))
                //.tap(nextPath -> log.info("score = {}, path = {}", nextPath.score(maxMinute), nextPath))
                .maxByOptional(path1 -> path1.score(maxMinute))
                .orElse(newPath);
    }

    @Data
    @EqualsAndHashCode(of = "id")
    @Builder
    private static class Valve {
        private String id;
        private int rate;
        private MutableSet<Valve> children;

        public static Valve ofId(String id) {
            return Valve.builder().id(id).build();
        }

        public static Valve of(String line) {
            Matcher matcher = pattern.matcher(line);
            matcher.find();

            MutableSet<Valve> children = Arrays.stream(matcher.group("ids").split(", "))
                    .map(id -> valves.computeIfAbsent(id, Valve::ofId))
                    .collect(Collectors2.toSet());
            String id = matcher.group("id");
            int rate = Integer.parseInt(matcher.group("rate"));

            Valve valve = valves.computeIfAbsent(id, Valve::ofId);
            valve.setChildren(children);
            valve.setRate(rate);
            return valve;
        }

        @Override
        public String toString() {
            return id;
        }
    }

    private record Path(ImmutableList<Valve> path, ImmutableList<Valve> orderOfOpening) {
        public static Path of(ImmutableList<Valve> path, ImmutableList<Valve> orderOfOpening) {
            return new Path(path, orderOfOpening);
        }

        public boolean contains(Valve valve) {
            return orderOfOpening.contains(valve);
        }

        public boolean exceedsLimit(int maxMinute) {
            return path.size() + orderOfOpening.size() > maxMinute;
        }

        public long score(int maxMinute) {
            AtomicInteger remainingMinutes = new AtomicInteger(maxMinute);
            Set<Valve> opened = Sets.mutable.empty();

            return path.collectInt(valve -> {
                if (remainingMinutes.get() <= 1) {
                    return 0;
                }

                if (!opened.contains(valve) && valve.getRate() != 0 && orderOfOpening.reject(opened::contains).getFirst().equals(valve)) {
                    opened.add(valve);
                    int i = remainingMinutes.decrementAndGet();
                    remainingMinutes.decrementAndGet();
                    return i * valve.getRate();
                }
                remainingMinutes.decrementAndGet();
                return 0;
            }).sum();
        }
    }
}
