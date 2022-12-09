package com.lfl.advent2022.days;

import com.lfl.advent2022.LinesConsumer;
import com.lfl.advent2022.utils.Point;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.collector.Collectors2;
import org.eclipse.collections.impl.factory.Lists;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

@Slf4j
@Service
public class Day9 implements LinesConsumer {
    @Override
    public void consume(List<String> lines) {
        MutableList<Move> moves = lines.stream()
                .map(Move::of)
                .collect(Collectors2.toList());

        MutableSet<Point> visited = moveRope(moves, Lists.mutable.withNValues(2, () -> Point.ZERO));
        log.info("size = {}", visited.size());

        visited = moveRope(moves, Lists.mutable.withNValues(10, () -> Point.ZERO));
        log.info("size = {}", visited.size());
    }

    private MutableSet<Point> moveRope(MutableList<Move> moves, MutableList<Point> knots) {
        return moves.flatCollect(move -> move.move(knots))
                .toSet();
    }

    public static MutableSet<Point> getAdjacents(Point point) {
        return Arrays.stream(Direction.values())
                .map(direction -> direction.move(point))
                .collect(Collectors2.toSet());
    }

    private record Move(Direction direction, int quantity) {

        public static Move of(String line) {
            return new Move(Direction.of(line.split(" ")[0]), Integer.parseInt(line.split(" ")[1]));
        }

        public MutableSet<Point> move(MutableList<Point> knots) {
            MutableSet<Point> visited = Sets.mutable.of(knots.getLast());

            IntStream.range(0, quantity)
                    .forEach(count -> {
                        knots.set(0, direction.move(knots.getFirst()));
                        IntStream.range(1, knots.size())
                                .forEach(index -> knots.set(index, moveTail(knots.get(index), knots.get(index - 1))));
                        visited.add(knots.getLast());
                    });
            return visited;
        }

        private static Point moveTail(Point tail, Point head) {
            MutableSet<Point> adjacents = getAdjacents(tail);
            if (tail.equals(head) || adjacents.contains(head)) {
                return tail;
            }

            return adjacents.minBy(p -> p.distance1(head));
        }
    }

    private enum Direction {
        UP("U", p -> Point.of(p.x(), p.y() + 1)),
        UP_LEFT("Q", p -> Point.of(p.x() - 1, p.y() + 1)),
        LEFT("L", p -> Point.of(p.x() - 1, p.y())),
        DOWN_LEFT("W", p -> Point.of(p.x() - 1, p.y() - 1)),
        DOWN("D", p -> Point.of(p.x(), p.y() - 1)),
        DOWN_RIGHT("E", p -> Point.of(p.x() + 1, p.y() - 1)),
        RIGHT("R", p -> Point.of(p.x() + 1, p.y())),
        UP_RIGHT("T", p -> Point.of(p.x() + 1, p.y() + 1));

        private final String code;
        private final Function<Point, Point> mover;

        Direction(String code, Function<Point, Point> mover) {
            this.code = code;
            this.mover = mover;
        }

        public static Direction of(String code) {
            return Arrays.stream(Direction.values())
                    .filter(direction -> direction.code.equals(code))
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException(code));
        }

        public Point move(Point point) {
            return this.mover.apply(point);
        }
    }
}
