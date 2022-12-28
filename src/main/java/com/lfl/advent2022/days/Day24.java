package com.lfl.advent2022.days;

import com.lfl.advent2022.LinesConsumer;
import com.lfl.advent2022.utils.Point;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.collector.Collectors2;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.utility.ArrayIterate;

import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.function.Function;

@Slf4j
public class Day24 implements LinesConsumer {

    private static final ImmutableSet<String> validBlizzard = Sets.immutable.of("^", ">", "v", "<");

    private static final MutableMap<Integer, MutableSet<Pair<Point, Blizzard>>> blizzards = Maps.mutable.empty();
    private static final MutableMap<Integer, MutableSet<Point>> blizzards2 = Maps.mutable.empty();
    private static final MutableMap<Point, Tile> map = Maps.mutable.empty();
    private static IntSummaryStatistics rowStatistics;
    private static IntSummaryStatistics columnStatistics;
    private int lcm;
    private Point end;

    @Override
    public void consume(MutableList<String> lines) {
        buildMap(lines);

        rowStatistics = map.keysView().collectInt(Point::row).summaryStatistics();
        columnStatistics = map.keysView().collectInt(Point::column).summaryStatistics();

        Point start = Interval.fromTo(columnStatistics.getMin(), columnStatistics.getMax())
                .collect(column -> Point.of(0, column))
                .detect(p -> map.get(p) == Tile.EMPTY);

        end = Interval.fromTo(columnStatistics.getMin(), columnStatistics.getMax())
                .collect(column -> Point.of(rowStatistics.getMax(), column))
                .detect(p -> map.get(p) == Tile.EMPTY);

        log.info("start = {}", start);
        log.info("end = {}", end);

        lcm = ArithmeticUtils.lcm(rowStatistics.getMax() - 1, columnStatistics.getMax() - 1);

        for (int i = 0; i < lcm; i++) {
            MutableSet<Pair<Point, Blizzard>> blizzardSet = blizzards.get(i);
            blizzards.put(i + 1, moveBlizzards(blizzardSet));
        }

        blizzards.forEach((round, set) -> blizzards2.put(round, set.stream().map(Pair::getOne).collect(Collectors2.toSet())));

        log.info("blizzards computed");

        int toGoal = findPath(start, end, 0);
        log.info("round to goal = {}", toGoal);

        int backToStart = findPath(end, start, toGoal);
        log.info("back to start = {}", backToStart);

        int backToGoal = findPath(start, end, backToStart);
        log.info("back to goal = {}", backToGoal);
    }

    private int findPath(Point start, Point end, int round) {
        MutableSet<Point> path = Sets.mutable.of(start);

        while (!path.contains(end)) {
            round++;
            int finalRound = round;
            path = path.select(p -> !getBlizzards(finalRound).contains(p))
                    .flatCollect(p -> findAdjacents(p, finalRound));
        }

        return round + 1;
    }

    private MutableSet<Point> findAdjacents(Point start, int round) {
        MutableSet<Point> possible = Arrays.stream(Blizzard.values())
                .map(direction -> direction.move(start))
                .filter(map::containsKey)
                .filter(p -> map.get(p) == Tile.EMPTY)
                .filter(p -> !getBlizzards(round + 1).contains(p))
                .collect(Collectors2.toSet());

        if (!getBlizzards(round + 1).contains(start)) {
            possible.add(start);
        }
        return possible;
    }

    private MutableSet<Point> getBlizzards(Integer round) {
        return blizzards2.get(Math.floorMod(round, lcm));
    }

    private MutableSet<Pair<Point, Blizzard>> moveBlizzards(MutableSet<Pair<Point, Blizzard>> blizzards) {
        return blizzards.collect(this::moveBlizzard);
    }

    private Pair<Point, Blizzard> moveBlizzard(Pair<Point, Blizzard> pair) {
        return moveBlizzard(pair.getOne(), pair.getTwo());
    }

    private static Pair<Point, Blizzard> moveBlizzard(Point p, Blizzard blizzard) {
        Point nextPoint = blizzard.move(p);
        if (map.get(nextPoint) == Tile.WALL) {
            nextPoint = blizzard.reset(p);
        }
        return Tuples.pair(nextPoint, blizzard);
    }

    private static void buildMap(MutableList<String> lines) {
        lines.forEachWithIndex((line, row) -> ArrayIterate.collect(line.split(""), s -> s)
                .forEachWithIndex((code, column) -> {
                    Point point = Point.ofRC(row, column);
                    if (validBlizzard.contains(code)) {
                        blizzards.computeIfAbsent(0, k -> Sets.mutable.empty()).add(Tuples.pair(point, Blizzard.of(code)));
                    }

                    map.put(point, Tile.of(code));
                })
        );
    }

    enum Blizzard {
        UP("^", p -> Point.of(p.row() - 1, p.column()), p -> Point.of(rowStatistics.getMax() - 1, p.column())),
        RIGHT(">", p -> Point.of(p.row(), p.column() + 1), p -> Point.of(p.row(), 1)),
        DOWN("v", p -> Point.of(p.row() + 1, p.column()), p -> Point.of(1, p.column())),
        LEFT("<", p -> Point.of(p.row(), p.column() - 1), p -> Point.of(p.row(), columnStatistics.getMax() - 1));

        private final String code;
        private final Function<Point, Point> mover;
        private final Function<Point, Point> reseter;

        Blizzard(String code, Function<Point, Point> mover, Function<Point, Point> reseter) {
            this.code = code;
            this.mover = mover;
            this.reseter = reseter;
        }

        public static Blizzard of(String code) {
            return Arrays.stream(Blizzard.values())
                    .filter(blizzard -> blizzard.code.equals(code))
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException(code));
        }

        public Point move(Point p) {
            return this.mover.apply(p);
        }

        public Point reset(Point p) {
            return this.reseter.apply(p);
        }
    }

    enum Tile {
        WALL("#"),
        EMPTY(".");

        private final String code;

        Tile(String code) {
            this.code = code;
        }

        public static Tile of(String code) {
            return Arrays.stream(Tile.values())
                    .filter(tile -> tile.code.equals(code))
                    .findAny()
                    .orElse(EMPTY);
        }
    }
}
