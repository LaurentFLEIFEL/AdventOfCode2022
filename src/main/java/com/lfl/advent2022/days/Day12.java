package com.lfl.advent2022.days;

import com.lfl.advent2022.LinesConsumer;
import com.lfl.advent2022.utils.Point;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.collector.Collectors2;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@Slf4j
@Service
public class Day12 implements LinesConsumer {

    private MutableMap<Point, Character> map;

    @Override
    public void consume(List<String> lines) {
        map = buildMap(lines);

        Point start = map.detect((p, c) -> c == 'S').getOne();
        Point end = map.detect((p, c) -> c == 'E').getOne();

        map.put(start, 'a');
        map.put(end, 'z');

        int step = findStep(start, end);
        log.info("step = {}", step);

        Set<Point> startingPoints = map.select((p, c) -> c == 'a').keySet();
        Integer minStep = startingPoints.stream()
                .map(point -> findStep(point, end))
                .min(Comparator.naturalOrder())
                .orElseThrow(IllegalArgumentException::new);

        log.info("minStep = {}", minStep);
    }

    private static MutableMap<Point, Character> buildMap(List<String> lines) {
        MutableMap<Point, Character> map = Maps.mutable.empty();
        for (int x = 0; x < lines.size(); x++) {
            String line = lines.get(x);
            for (int y = 0; y < line.length(); y++) {
                map.put(Point.of(x, y), line.charAt(y));
            }
        }
        return map;
    }

    private static final Map<Point, MutableSet<Point>> adjacents = Maps.mutable.empty();

    private int findStep(Point start, Point end) {
        MutableSet<Point> visited = Sets.mutable.of(start);
        int step = 0;
        while (!visited.contains(end)) {
            step++;

            MutableSet<Point> newVisited = visited.flatCollect(point -> adjacents.computeIfAbsent(point, this::findAdjacents)
                            .reject(visited::contains)
            );
            if (newVisited.isEmpty()) {
                return Integer.MAX_VALUE;
            }

            visited.addAll(newVisited);
        }
        return step;
    }

    private MutableSet<Point> findAdjacents(Point point) {
        return Arrays.stream(Direction.values())
                .map(direction -> direction.move(point))
                .filter(map::containsKey)
                .filter(point1 -> map.get(point1) <= map.get(point) + 1)
                .collect(Collectors2.toSet());
    }

    private enum Direction {
        UP(p -> Point.of(p.x() + 1, p.y())),
        LEFT(p -> Point.of(p.x(), p.y() - 1)),
        DOWN(p -> Point.of(p.x() - 1, p.y())),
        RIGHT(p -> Point.of(p.x(), p.y() + 1));

        private final Function<Point, Point> mover;

        Direction(Function<Point, Point> mover) {
            this.mover = mover;
        }

        public Point move(Point point) {
            return this.mover.apply(point);
        }
    }
}
