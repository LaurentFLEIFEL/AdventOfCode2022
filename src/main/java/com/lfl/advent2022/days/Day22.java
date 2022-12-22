package com.lfl.advent2022.days;

import com.lfl.advent2022.LinesConsumer;
import com.lfl.advent2022.utils.Point;
import com.lfl.advent2022.utils.StringHelper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.primitive.ImmutableIntList;
import org.eclipse.collections.impl.collector.Collectors2;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

@Slf4j
@Service
public class Day22 implements LinesConsumer {

    private static final Map<Point, Tile> map = Maps.mutable.empty();
    private static final Pattern PATTERN = Pattern.compile("([RL]+)");

    private static final boolean isPartOne = false;

    @Setter
    private static int edgeSize = 50;
    private static final Map<Position, Position> edgeMapping = Maps.mutable.empty();

    @Override
    public void consume(List<String> lines) {
        lines.stream()
                .takeWhile(s -> !s.isEmpty())
                .collect(Collectors2.toList())
                .forEachWithIndex((line, row) -> ArrayIterate.forEachWithIndex(line.split(""), (code, column) -> map.put(Point.of(row + 1, column + 1), Tile.of(code))));

        String path = lines.stream()
                .dropWhile(s -> !s.isEmpty())
                .filter(s -> !s.isEmpty())
                .toList()
                .get(0);

        ImmutableIntList forwards = StringHelper.ints(path);
        List<Direction> changeDirection = PATTERN.matcher(path)
                .results()
                .map(MatchResult::group)
                .map(Direction::of)
                .toList();


        Position start = Position.of(findStart(), Facing.RIGHT);
        findEdgeMapping(start);
        Position result = apply(start, forwards, changeDirection);

        log.info("result = {}", result);
        log.info("score = {}", result.score());
    }

    //thanks https://gist.github.com/progheal/a0339ab6d171a1dee13998708fbf792a
    private void findEdgeMapping(Position start) {
        Deque<Position> unmatchedPositions = new ArrayDeque<>();
        Deque<TurnType> unmatchedTurn = new ArrayDeque<>();
        unmatchedTurn.addLast(TurnType.EDGE_RIGHT);
        boolean shouldAdd = false;
        Position current = start;
        do {
            //add the current edge to either the unmatched or to the mapping
            if (!shouldAdd) {
                for (int i = 0; i < edgeSize; i++) {
                    unmatchedPositions.addLast(current);
                    current = current.moveOneStep();
                }
            } else {
                for (int i = 0; i < edgeSize; i++) {
                    Position position = unmatchedPositions.pollLast();
                    edgeMapping.put(position.changeFacing(Direction.LEFT), current.changeFacing(Direction.RIGHT));
                    edgeMapping.put(current.changeFacing(Direction.LEFT), position.changeFacing(Direction.RIGHT));
                    current = current.moveOneStep();
                }
            }

            //check type of turn
            Tile now = map.getOrDefault(current.point(), Tile.VOID);
            Position left = current.changeFacing(Direction.LEFT).moveOneStep();
            Tile leftTile = map.getOrDefault(left.point(), Tile.VOID);
            TurnType edgeTurn = TurnType.EDGE_LEFT;

            if (leftTile == Tile.VOID && now == Tile.VOID) {
                edgeTurn = TurnType.EDGE_RIGHT;
            } else if (leftTile == Tile.VOID) {
                edgeTurn = TurnType.EDGE_STRAIGHT;
            }

            //match turn types or add to unmatched
            if (shouldAdd) {
                TurnType lastUnmatchedTurn = unmatchedTurn.pollLast();
                if (lastUnmatchedTurn.ordinal() - 1 + edgeTurn.ordinal() - 1 == 1) {
                    if (unmatchedTurn.isEmpty()) {
                        unmatchedTurn.addLast(edgeTurn);
                        shouldAdd = false;
                    }
                } else {
                    unmatchedTurn.addLast(TurnType.EDGE_STRAIGHT);
                    shouldAdd = false;
                }
            } else {
                if (edgeTurn == TurnType.EDGE_LEFT) {
                    shouldAdd = true;
                } else {
                    unmatchedTurn.addLast(edgeTurn);
                }
            }

            //change edge start
            if (edgeTurn == TurnType.EDGE_RIGHT) {
                //we are one step ahead in the void so we turn back and we should have turned right
                current = current.changeFacing(Direction.RIGHT).changeFacing(Direction.RIGHT).moveOneStep().changeFacing(Direction.LEFT);
            } else if (edgeTurn == TurnType.EDGE_LEFT) {
                current = current.changeFacing(Direction.LEFT).moveOneStep();
            }
        } while (!current.equals(start));
    }

    private enum TurnType {
        EDGE_LEFT,
        EDGE_STRAIGHT,
        EDGE_RIGHT
    }

    private static Position apply(Position start, ImmutableIntList forwards, List<Direction> changeDirection) {
        Position result = start;
        for (int index = 0; index < forwards.size(); index++) {
            result = isPartOne ? result.moveForward(forwards.get(index)) : result.moveForward2(forwards.get(index));
            if (index < changeDirection.size()) {
                result = result.changeFacing(changeDirection.get(index));
            }
        }

        return result;
    }

    private static Point findStart() {
        for (int column = 1; column < 100; column++) {
            if (map.getOrDefault(Point.of(1, column), Tile.VOID) == Tile.OPEN) {
                return Point.of(1, column);
            }
        }
        return null;
    }

    private record Position(Point point, Facing facing) {

        public static Position of(Point point, Facing facing) {
            return new Position(point, facing);
        }

        public Position changeFacing(Direction direction) {
            return Position.of(point, direction.change(facing));
        }

        public Position moveOneStep() {
            return Position.of(facing.move(point), facing);
        }

        public Position moveForward(int length) {
            Point newPoint = point;
            for (int i = 0; i < length; i++) {
                newPoint = facing.move(newPoint);
                if (Tile.WALL == map.getOrDefault(newPoint, Tile.VOID)) {
                    return Position.of(facing.opposing().move(newPoint), facing);
                }
                if (Tile.VOID == map.getOrDefault(newPoint, Tile.VOID)) {
                    Point previousPoint = facing.opposing().move(newPoint);
                    newPoint = previousPoint;
                    while (Tile.VOID != map.getOrDefault(newPoint, Tile.VOID)) {
                        newPoint = facing.opposing().move(newPoint);
                    }
                    newPoint = facing.move(newPoint);
                    if (Tile.WALL == map.getOrDefault(newPoint, Tile.VOID)) {
                        return Position.of(previousPoint, facing);
                    }
                }
            }

            return Position.of(newPoint, facing);
        }

        public Position moveForward2(int length) {
            Point newPoint = point;
            for (int i = 0; i < length; i++) {
                newPoint = facing.move(newPoint);
                if (Tile.WALL == map.getOrDefault(newPoint, Tile.VOID)) {
                    return Position.of(facing.opposing().move(newPoint), facing);
                }
                if (Tile.VOID == map.getOrDefault(newPoint, Tile.VOID)) {
                    Point previousPoint = facing.opposing().move(newPoint);
                    Position position = edgeMapping.get(Position.of(previousPoint, facing));
                    if (Tile.WALL == map.getOrDefault(position.point(), Tile.VOID)) {
                        return Position.of(previousPoint, facing);
                    }
                    return position.moveForward2(length - i - 1);
                }
            }

            return Position.of(newPoint, facing);
        }

        public int score() {
            return 1000 * point.x() + 4 * point.y() + facing.ordinal();
        }
    }

    private enum Facing {
        RIGHT(p -> Point.of(p.x(), p.y() + 1)),
        DOWN(p -> Point.of(p.x() + 1, p.y())),
        LEFT(p -> Point.of(p.x(), p.y() - 1)),
        UP(p -> Point.of(p.x() - 1, p.y()));

        private final Function<Point, Point> mover;

        Facing(Function<Point, Point> mover) {
            this.mover = mover;
        }

        public Point move(Point point) {
            return this.mover.apply(point);
        }

        public Facing opposing() {
            return Facing.values()[Math.floorMod(this.ordinal() + 2, Facing.values().length)];
        }
    }

    private enum Direction {
        RIGHT("R", facing -> Facing.values()[Math.floorMod(facing.ordinal() + 1, Facing.values().length)]),
        LEFT("L", facing -> Facing.values()[Math.floorMod(facing.ordinal() - 1, Facing.values().length)]);

        private final String code;
        private final Function<Facing, Facing> changer;

        Direction(String code, Function<Facing, Facing> changer) {
            this.code = code;
            this.changer = changer;
        }

        public static Direction of(String code) {
            return Arrays.stream(Direction.values())
                    .filter(tile -> tile.code.equals(code))
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException(code));
        }

        public Facing change(Facing facing) {
            return this.changer.apply(facing);
        }
    }

    private enum Tile {
        OPEN("."),
        WALL("#"),
        VOID(" ");

        private final String code;

        Tile(String code) {
            this.code = code;
        }

        public static Tile of(String code) {
            return Arrays.stream(Tile.values())
                    .filter(tile -> tile.code.equals(code))
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException(code));
        }
    }
}
