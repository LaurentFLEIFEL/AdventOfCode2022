package com.lfl.advent2022.days;

import com.lfl.advent2022.LinesConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

@Slf4j
@Service
public class Day19_2 implements LinesConsumer {

    private static final Pattern pattern = Pattern.compile("Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.");


    private List<int[][]> blueprints;
    private int[] maxRobots;
    private int maxGeodes, output;

    @Override
    public void consume(List<String> lines) {
        blueprints = new ArrayList<>();
        output = byPart(0, 1);
        lines.forEach(this::constructBlueprint);
        findBlueprintScores();
        log.info("output = {}", output);
    }

    private void constructBlueprint(String line) {

        Matcher matcher = pattern.matcher(line);
        matcher.find();

        blueprints.add(
                new int[][] {
                        new int[] {parseInt(matcher.group(2)), 0, 0},
                        new int[] {parseInt(matcher.group(3)), 0, 0},
                        new int[] {parseInt(matcher.group(4)), parseInt(matcher.group(5)), 0},
                        new int[] {parseInt(matcher.group(6)), 0, parseInt(matcher.group(7))},
                });
    }

    private void findBlueprintScores() {
        for (int i = 0; i < byPart(blueprints.size(), 3); i++) {
            findMaxRobots(blueprints.get(i));
            findMaxGeodes(blueprints.get(i), new int[4], new int[] {1, 0, 0, 0}, byPart(24, 32));
            System.out.println("id = " + (i + 1) + ", geodes = " + maxGeodes);
            if (isPartOne()) output += (i + 1) * maxGeodes;
            else output *= maxGeodes;
            maxGeodes = 0;
        }
    }

    private int byPart(int i1, int i2) {
        return isPartOne() ? i1 : i2;
    }

    private boolean isPartOne() {
        return false;
    }

    private void findMaxRobots(int[][] blueprint) {
        maxRobots = new int[3];
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 4; i++) {
                maxRobots[j] = Math.max(maxRobots[j], blueprint[i][j]);
            }
        }
    }

    private int findMaxGeodes(int[][] blueprint, int[] resources, int[] robots, int time) {
        int[] resourceCopy, robotCopy;
        int wait, plannedGeodes;

        plannedGeodes = resources[3] + robots[3] * time;
        if (plannedGeodes + (time * time - time) / 2 <= maxGeodes) return 0;

        for (int i = 0; i < 4; i++) {
            if (i < 3 && maxRobots[i] <= robots[i]) continue;
            wait = timeForResources(blueprint[i], resources, robots) + 1;
            if (time - wait < 1) continue;

            resourceCopy = resources.clone();
            robotCopy = robots.clone();

            for (int j = 0; j < 4; j++) resourceCopy[j] += robots[j] * wait;
            for (int j = 0; j < 3; j++) resourceCopy[j] -= blueprint[i][j];
            robotCopy[i]++;
            plannedGeodes =
                    Math.max(plannedGeodes, findMaxGeodes(blueprint, resourceCopy, robotCopy, time - wait));
        }

        maxGeodes = Math.max(maxGeodes, plannedGeodes);
        return plannedGeodes;
    }

    private int timeForResources(int[] cost, int[] resources, int[] robots) {
        int maxTime = 0;
        for (int i = 0; i < 3; i++) {
            if (cost[i] == 0) continue;
            if (robots[i] == 0) return 1000;
            maxTime = Math.max(maxTime, (cost[i] - resources[i] - 1 + robots[i]) / robots[i]);
        }
        return maxTime;
    }
}
