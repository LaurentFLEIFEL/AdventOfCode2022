package com.lfl.advent2022.days;

import com.lfl.advent2022.LinesConsumer;
import com.lfl.advent2022.utils.Point;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IteratorUtils;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.collector.Collectors2;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@Service
public class Day17 implements LinesConsumer {

    private static final String rock1 = "####";
    private static final String rock2 = """
            .#.
            ###
            .#.""";
    private static final String rock3 = """
            ..#
            ..#
            ###""";
    private static final String rock4 = """
            #
            #
            #
            #""";
    private static final String rock5 = """
            ##
            ##""";

    private static final MutableMap<Point, String> map = Maps.mutable.empty();
    private static final Map<String, MutableList<Integer>> xPositionsByRock = Maps.mutable.empty();

    @Setter
    private static int maxSize = 324;


    @Override
    public void consume(MutableList<String> lines) {
        MutableList<Jet> jetPattern = ArrayIterate.collect(lines.get(0).split(""), Jet::of);

        Iterator<Jet> jetIterator = IteratorUtils.loopingListIterator(jetPattern);
        Iterator<String> rockIterator = IteratorUtils.loopingListIterator(Lists.mutable.of(rock1, rock2, rock3, rock4, rock5));
        MutableList<Snapshot> snapshots = Lists.mutable.empty();

        int maxCount = 5000;
        int currentHeight = 0;
        boolean done = false;

        for (int count = 0; count < maxCount; count++) {
            //log.info("count = {}", count);
            //printMap();
            String rock = rockIterator.next();
            Point currentPosition = Point.of(2, currentHeight + 3);

            while (true) {
                Jet jet = jetIterator.next();
                if (canApply(jet, rock, currentPosition)) {
                    //log.info("jet {} can apply", jet);
                    currentPosition = jet.move(currentPosition);
                }

                if (canMove(rock, currentPosition)) {
                    //log.info("can Move from {}", currentPosition);
                    currentPosition = moveDown(currentPosition);
                } else {
                    break;
                }
            }
            //log.info("current position = {}", currentPosition);
            put(rock, currentPosition);
            xPositionsByRock.computeIfAbsent(rock, key -> Lists.mutable.empty()).add(currentPosition.x());
            currentHeight = height();

            if (count % 5 == 4) {
                ImmutableList<Integer> list = Lists.immutable.of(
                        xPositionsByRock.get(rock1).getLast(),
                        xPositionsByRock.get(rock2).getLast(),
                        xPositionsByRock.get(rock3).getLast(),
                        xPositionsByRock.get(rock4).getLast(),
                        xPositionsByRock.get(rock5).getLast()
                );

                if (count >= 1500 && !done) {
                    if (!snapshots.isEmpty() && snapshots.get(0).positions().equals(list)) {
                        done = true;
                    }
                    snapshots.add(new Snapshot(list, count / 5, currentHeight));
                }
            }
        }


        log.info("height = {}", currentHeight);

        BigInteger target = (new BigInteger("1000000000000", 10)).add(BigInteger.ONE.negate());
        BigInteger index = target.divide(BigInteger.valueOf(5));

        int cycleLength = snapshots.size() - 1;
        BigInteger first = BigInteger.valueOf(snapshots.get(0).index());
        BigInteger newIndex = index.add(first.negate()).mod(BigInteger.valueOf(cycleLength));

        Snapshot snapshot = snapshots.get(newIndex.intValue());

        BigInteger remainder = BigInteger.valueOf(snapshot.height());
        BigInteger cycle = BigInteger.valueOf(snapshots.get(cycleLength).height() - snapshots.get(0).height());
        BigInteger nbrOfCycle = index.add(first.negate()).divide(BigInteger.valueOf(cycleLength));

        BigInteger result = nbrOfCycle.multiply(cycle).add(remainder);

        log.info("result = {}", result);
        //printMap();
    }

    @Builder
    private record Snapshot(ImmutableList<Integer> positions, int index, int height) {
    }

    private void put(String rock, Point currentPosition) {
        MutableList<String> reversedRock = rock.lines().collect(Collectors2.toList()).reverseThis();
        reversedRock.forEachWithIndex((line, deltaY) -> ArrayIterate.forEachWithIndex(line.split(""), (tile, deltaX) -> {
            if (!"#".equals(tile)) return;
            int x = currentPosition.x() + deltaX;
            int y = currentPosition.y() + deltaY;
            map.put(Point.of(x, y), "#");
        }));
    }

    private boolean canApply(Jet jet, String rock, Point currentPosition) {
        return retrieveEdge(jet, rock)
                .collect(point -> Point.of(currentPosition.x() + point.x(), currentPosition.y() + point.y()))
                .noneSatisfy(point -> point.x() == jet.getLimit() || "#".equals(map.get(point)));
    }

    private MutableSet<Point> retrieveEdge(Jet jet, String rock) {
        MutableList<String> reversedRock = rock.lines().collect(Collectors2.toList()).reverseThis();
        return reversedRock.collectWithIndex((line, y) -> jet.select(ArrayIterate.collect(line.split(""), s -> s)
                .<Optional<Point>>collectWithIndex((tile, x) -> !"#".equals(tile) ? Optional.empty() : Optional.of(Point.of(x, y)))
                .select(Optional::isPresent)
                .collect(Optional::get)
                .collect(jet::move))
        ).toSet();
    }

    private boolean canMove(String rock, Point currentPosition) {
        if (currentPosition.y() == 0) {
            return false;
        }
        if (!rock2.equals(rock)) {
            int width = widthOf(rock);

            return IntStream.range(0, width)
                    .mapToObj(index -> Point.of(currentPosition.x() + index, currentPosition.y() - 1))
                    .map(map::get)
                    .noneMatch("#"::equals);
        }

        Point p1 = Point.of(currentPosition.x(), currentPosition.y());
        Point p2 = Point.of(currentPosition.x() + 1, currentPosition.y() - 1);
        Point p3 = Point.of(currentPosition.x() + 2, currentPosition.y());
        return Stream.of(p1, p2, p3)
                .map(map::get)
                .noneMatch("#"::equals);
    }

    private int height() {
        return map.select((point, tile) -> "#".equals(tile))
                .keysView()
                .collectInt(Point::y)
                .collectInt(i -> i + 1, IntLists.mutable.empty())
                .maxIfEmpty(0);
    }

    private static Point moveDown(Point point) {
        return Point.of(point.x(), point.y() - 1);
    }

    private static int widthOf(String rock) {
        return rock.lines()
                .mapToInt(String::length)
                .max()
                .getAsInt();
    }

    private enum Jet {
        LEFT("<", p -> Point.of(p.x() - 1, p.y()), list -> list.minBy(Point::x), -1),
        RIGHT(">", p -> Point.of(p.x() + 1, p.y()), list -> list.maxBy(Point::x), 7);

        private final String code;
        private final Function<Point, Point> mover;
        private final Function<MutableList<Point>, Point> selector;
        @Getter
        private final int limit;

        Jet(String code, Function<Point, Point> mover, Function<MutableList<Point>, Point> selector, int limit) {
            this.code = code;
            this.mover = mover;
            this.selector = selector;
            this.limit = limit;
        }

        public static Jet of(String code) {
            return Arrays.stream(Jet.values())
                    .filter(jet -> jet.code.equals(code))
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException(code));
        }

        public Point move(Point point) {
            return this.mover.apply(point);
        }

        public Point select(MutableList<Point> list) {
            return this.selector.apply(list);
        }
    }

    private void printMap() {
        int maxY = map.keysView().collectInt(Point::y).maxIfEmpty(0);
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (int y = maxY; y >= 0; y--) {
            for (int x = 0; x < 7; x++) {
                Point of = Point.of(x, y);
                String tile = map.getOrDefault(of, ".");
                sb.append(tile);
            }
            sb.append("\n");
        }

        log.info("Map = {}", sb.toString());
    }
}
