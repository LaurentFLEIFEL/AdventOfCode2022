package com.lfl.advent2022.days;

import com.lfl.advent2022.LinesConsumer;
import com.lfl.advent2022.utils.Point3;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.collector.Collectors2;
import org.eclipse.collections.impl.list.Interval;
import org.springframework.stereotype.Service;

import java.util.IntSummaryStatistics;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;

@Slf4j
@Service
public class Day18 implements LinesConsumer {

    @SuppressWarnings("unchecked")
    private static final ImmutableSet<Function<Point3, Point3>> DIRECTIONS = Sets.immutable.of(
            p -> Point3.of(p.x() + 1, p.y(), p.z()),
            p -> Point3.of(p.x() - 1, p.y(), p.z()),
            p -> Point3.of(p.x(), p.y() + 1, p.z()),
            p -> Point3.of(p.x(), p.y() - 1, p.z()),
            p -> Point3.of(p.x(), p.y(), p.z() + 1),
            p -> Point3.of(p.x(), p.y(), p.z() - 1)
    );

    @Override
    public void consume(MutableList<String> lines) {
        MutableSet<Point3> cubes = lines.stream()
                .map(line -> line.split(","))
                .map(split -> Point3.of(parseInt(split[0]), parseInt(split[1]), parseInt(split[2])))
                .collect(Collectors2.toSet());
        log.info("cubes = {}", cubes.size());

        long sum = countEdges(cubes);
        log.info("sum = {}", sum);

        Statistics stats = Statistics.of(cubes);

        MutableSet<Point3> eligible = buildCubeEnvelope(stats).difference(cubes);
        MutableSet<Point3> outerCubes = retrieveOuterCubes(eligible, stats);
        MutableSet<Point3> innerCubes = eligible.difference(outerCubes);
        log.info("innerCubes = {}", innerCubes.size());

        long invalidEdges = countEdges(innerCubes);
        log.info("invalidEdges = {}", invalidEdges);

        log.info("sum2 = {}", sum - invalidEdges);
    }

    private static MutableSet<Point3> buildCubeEnvelope(Statistics stats) {
        return Interval.fromTo(stats.minX(), stats.maxX())
                .flatCollect(x -> Interval.fromTo(stats.minY(), stats.maxY())
                        .flatCollect(y -> Interval.fromTo(stats.minZ(), stats.maxZ())
                                .collect(z -> Point3.of(x, y, z))
                        )
                )
                .toSet();
    }

    private static MutableSet<Point3> retrieveOuterCubes(MutableSet<Point3> eligible, Statistics stats) {
        MutableSet<Point3> linkedToEdge = Sets.mutable.empty();

        int previousSize = -1;
        int size = linkedToEdge.size();

        while (previousSize != size) {
            previousSize = linkedToEdge.size();
            eligible.stream()
                    .filter(p -> isEdge(p, stats) || adjacentsOf(p).anyMatch(linkedToEdge::contains))
                    .forEach(linkedToEdge::add);

            size = linkedToEdge.size();
        }

        return linkedToEdge;
    }

    private static long countEdges(MutableSet<Point3> points) {
        return points.stream()
                .mapToLong(p -> adjacentsOf(p).filter(adjacent -> !points.contains(adjacent)).count())
                .sum();
    }

    private static Stream<Point3> adjacentsOf(Point3 p) {
        return DIRECTIONS.stream().map(mover -> mover.apply(p));
    }

    private static boolean isEdge(Point3 p, Statistics stats) {
        return (p.x() == stats.minX() || p.x() == stats.maxX())
                || (p.y() == stats.minY() || p.y() == stats.maxY())
                || (p.z() == stats.minZ() || p.z() == stats.maxZ());
    }

    private record Statistics(IntSummaryStatistics xStatistics, IntSummaryStatistics yStatistics, IntSummaryStatistics zStatistics) {
        public static Statistics of(MutableSet<Point3> cubes) {
            return new Statistics(cubes.collectInt(Point3::x).summaryStatistics(),
                    cubes.collectInt(Point3::y).summaryStatistics(),
                    cubes.collectInt(Point3::z).summaryStatistics());
        }

        public int minX() {
            return xStatistics.getMin();
        }

        public int minY() {
            return yStatistics.getMin();
        }

        public int minZ() {
            return zStatistics.getMin();
        }

        public int maxX() {
            return xStatistics.getMax();
        }

        public int maxY() {
            return yStatistics.getMax();
        }

        public int maxZ() {
            return zStatistics.getMax();
        }
    }
}
