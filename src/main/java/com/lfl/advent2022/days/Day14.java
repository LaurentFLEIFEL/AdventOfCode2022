package com.lfl.advent2022.days;

import com.lfl.advent2022.LinesConsumer;
import com.lfl.advent2022.utils.Point;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.collector.Collectors2;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.list.Interval;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

@Slf4j
@Service
public class Day14 implements LinesConsumer {

    private static final ImmutableList<Function<Point, Point>> DIRECTIONS = Lists.immutable.<Function<Point, Point>>empty()
            .newWith(p -> Point.of(p.x(), p.y() + 1))
            .newWith(p -> Point.of(p.x() - 1, p.y() + 1))
            .newWith(p -> Point.of(p.x() + 1, p.y() + 1));
    private static final Point START = Point.of(500, 0);
    private int maxY;

    @Override
    public void consume(List<String> lines) {
        MutableSet<Wall> walls = buildWalls(lines);

        maxY = walls.collect(Wall::yInterval)
                .collectInt(Interval::getLast)
                .max();

        MutableSet<Point> sands = makeSandsFall(walls);
        log.info("sand size = {}", sands.size());

        walls.add(new Wall(Interval.fromTo(Integer.MIN_VALUE / 2 + 1, Integer.MAX_VALUE / 2 - 1), Interval.fromTo(maxY + 2, maxY + 2)));
        MutableSet<Point> sands2 = makeSandsFall(walls);
        log.info("sand size = {}", sands2.size());
    }

    private static MutableSet<Wall> buildWalls(List<String> lines) {
        return lines.stream()
                .map(line -> line.replaceAll(" ", ""))
                .map(line -> line.split("->"))
                .flatMap(split -> IntStream.range(0, split.length - 1)
                        .mapToObj(index -> Wall.of(split[index], split[index + 1])))
                .collect(Collectors2.toSet());
    }

    private MutableSet<Point> makeSandsFall(MutableSet<Wall> walls) {
        MutableSet<Point> sands = Sets.mutable.empty();
        while (true) {
            Point previous = null;
            Point sand = START;
            while (!sand.equals(previous)) {
                previous = sand;
                Point finalSand = sand;
                sand = DIRECTIONS.collect(direction -> direction.apply(finalSand))
                        .reject(sands::contains)
                        .reject(p -> walls.anySatisfy(wall -> wall.contains(p)))
                        .getFirstOptional()
                        .orElse(sand);

                if (exceedYLimit(sand)) {
                    break;
                }
            }
            if (exceedYLimit(sand)) {
                break;
            }

            sands.add(sand);

            if (sand.equals(START)) {
                break;
            }
        }
        return sands;
    }

    private boolean exceedYLimit(Point sand) {
        return sand.y() > maxY + 2;
    }

    private record Wall(Interval xInterval, Interval yInterval) {
        public static Wall of(String firstPoint, String secondPoint) {
            String[] split1 = firstPoint.split(",");
            String[] split2 = secondPoint.split(",");
            Interval xInterval = Interval.fromTo(Integer.parseInt(split1[0]), Integer.parseInt(split2[0]));
            Interval yInterval = Interval.fromTo(Integer.parseInt(split1[1]), Integer.parseInt(split2[1]));

            return new Wall(xInterval, yInterval);
        }

        public boolean contains(Point point) {
            return xInterval.contains(point.x()) && yInterval.contains(point.y());
        }
    }
}
