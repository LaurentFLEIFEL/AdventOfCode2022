package com.lfl.advent2022.days;

import com.lfl.advent2022.LinesConsumer;
import com.lfl.advent2022.utils.Point;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IteratorUtils;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.impl.utility.ListIterate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.IntStream;

@Slf4j
@Service
public class Day23 implements LinesConsumer {

    private static final List<Direction> validDirection = Lists.mutable.of(Direction.N, Direction.S, Direction.W, Direction.E);
    private static final Iterator<Direction> firstDirections = IteratorUtils.loopingListIterator(validDirection);
    private static final Map<Direction, Set<Direction>> toBeChecked = Maps.mutable.<Direction, Set<Direction>>empty()
            .withKeyValue(Direction.N, Sets.mutable.of(Direction.N, Direction.NW, Direction.NE))
            .withKeyValue(Direction.S, Sets.mutable.of(Direction.S, Direction.SW, Direction.SE))
            .withKeyValue(Direction.W, Sets.mutable.of(Direction.W, Direction.NW, Direction.SW))
            .withKeyValue(Direction.E, Sets.mutable.of(Direction.E, Direction.SE, Direction.NE));


    @Override
    public void consume(List<String> lines) {
        MutableSet<Point> elves = retrieveElves(lines);
        log.info("elves = {}", elves.size());

        doRounds(elves);
    }

    private static void doRounds(MutableSet<Point> elves) {
        int round = 0;
        MutableMap<Point, Point> proposals = Maps.mutable.empty();
        do {
            //printMap(current);
            proposals.clear();
            Direction firstPriority = firstDirections.next();
            //first half
            elves.stream()
                    .map(elf -> Tuples.pair(elf, computeProposingPosition(elf, elves, firstPriority)))
                    .filter(pair -> !pair.getTwo().equals(pair.getOne()))
                    .forEach(pair -> proposals.put(pair.getOne(), pair.getTwo()));
            MutableBag<Point> duplicateProposals = proposals.collect(proposal -> proposal).selectDuplicates();
            proposals.removeIf((elf, proposal) -> duplicateProposals.contains(proposal));
            //second half
            proposals.forEachKey(elf -> {
                elves.remove(elf);
                elves.add(proposals.get(elf));
            });

            round++;
            log.info("round = {}, priority = {}", round, firstPriority);
            if (round == 10) {
                int result = computeEmptyTiles(elves);
                log.info("result = {}", result);
            }
        } while (proposals.size() > 0);
        //printMap(current);
    }

    private static void printMap(MutableSet<Point> elves) {
        IntSummaryStatistics rowStatistics = elves.collectInt(Point::row).summaryStatistics();
        IntSummaryStatistics columnStatistics = elves.collectInt(Point::column).summaryStatistics();

        StringBuilder builder = new StringBuilder();

        IntStream.range(rowStatistics.getMin(), rowStatistics.getMax() + 1)
                .forEach(row -> {
                    IntStream.range(columnStatistics.getMin(), columnStatistics.getMax() + 1)
                            .mapToObj(column -> elves.stream().anyMatch(p -> p.equals(Point.of(row, column))))
                            .forEach(contains -> builder.append(contains ? "#" : "."));
                    builder.append("\n");
                });

        log.info("map = \n{}", builder);
    }

    private static MutableSet<Point> retrieveElves(List<String> lines) {
        MutableSet<Point> elves = Sets.mutable.empty();
        ListIterate.forEachWithIndex(lines, (line, row) -> ArrayIterate.forEachWithIndex(line.split(""), (s, column) -> {
            if (s.equals(".")) {
                return;
            }

            elves.add(Point.ofRC(row, column));
        }));
        return elves;
    }

    private static int computeEmptyTiles(MutableSet<Point> elves) {
        IntSummaryStatistics rowStatistics = elves.collectInt(Point::row).summaryStatistics();
        IntSummaryStatistics columnStatistics = elves.collectInt(Point::column).summaryStatistics();
        return (rowStatistics.getMax() - rowStatistics.getMin() + 1) * (columnStatistics.getMax() - columnStatistics.getMin() + 1) - elves.size();
    }

    private static Point computeProposingPosition(Point position, MutableSet<Point> elves, Direction firstPriority) {
        //check adjacents
        if (Arrays.stream(Direction.values())
                .map(direction -> direction.move(position))
                .noneMatch(elves::contains)) {
            return position;
        }

        //check by priority
        for (int offset = 0; offset < validDirection.size(); offset++) {
            Direction direction = validDirection.get(Math.floorMod(validDirection.indexOf(firstPriority) + offset, validDirection.size()));
            if (toBeChecked.get(direction)
                    .stream()
                    .map(direction1 -> direction1.move(position))
                    .noneMatch(elves::contains)) {
                return direction.move(position);
            }
        }

        //don’t move
        return position;
    }

    private enum Direction {
        N(p -> p.add(Point.of(- 1, 0))),
        NW(p -> Point.of(p.x() - 1, p.y() - 1)),
        W(p -> Point.of(p.x(), p.y() - 1)),
        SW(p -> Point.of(p.x() + 1, p.y() - 1)),
        S(p -> Point.of(p.x() + 1, p.y())),
        SE(p -> Point.of(p.x() + 1, p.y() + 1)),
        E(p -> Point.of(p.x(), p.y() + 1)),
        NE(p -> Point.of(p.x() - 1, p.y() + 1));

        private final Function<Point, Point> mover;

        Direction(Function<Point, Point> mover) {
            this.mover = mover;
        }

        public Point move(Point point) {
            return this.mover.apply(point);
        }
    }
}
