package com.lfl.advent2022.days;

import com.lfl.advent2022.LinesConsumer;
import com.lfl.advent2022.utils.Point;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.set.MutableSet;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Slf4j
@Service
public class Day8 implements LinesConsumer {

    private int maxX;
    private int maxY;
    @Getter
    private long maxScenicScore;
    @Getter
    private MutableSet<Point> visiblePoints;

    @Override
    public void consume(List<String> lines) {
        maxX = lines.size();
        maxY = lines.get(0).length();

        computeVisiblePoints(lines);
        log.info("size = {}", visiblePoints.size());

        computeMaxScenicScore(lines);
        log.info("maxScenicScore = {}", maxScenicScore);
    }

    private void computeMaxScenicScore(List<String> lines) {
        maxScenicScore = -1;
        for (int x = 0; x < maxX; x++) {
            for (int y = 0; y < maxY; y++) {
                Point current = Point.of(x, y);
                long up = computeViewingDistance(current, lines, p -> Point.of(p.x() + 1, p.y()));
                long down = computeViewingDistance(current, lines, p -> Point.of(p.x() - 1, p.y()));
                long left = computeViewingDistance(current, lines, p -> Point.of(p.x(), p.y() + 1));
                long right = computeViewingDistance(current, lines, p -> Point.of(p.x(), p.y() - 1));
                long scenicScore = up * down * left * right;

                if (scenicScore > maxScenicScore) {
                    maxScenicScore = scenicScore;
                }
            }
        }
    }

    private void computeVisiblePoints(List<String> lines) {
        visiblePoints = Sets.mutable.empty();
        for (int x = 0; x < maxX; x++) {
            int maxUp = -1;
            int maxDown = -1;
            int maxLeft = -1;
            int maxRight = -1;
            for (int y = 0; y < maxY; y++) {
                int valueUp = extractHeight(lines, x, y);
                int valueDown = extractHeight(lines, x, maxY - y - 1);
                int valueLeft = extractHeight(lines, y, x);
                int valueRight = extractHeight(lines, maxY - y - 1, x);

                if (valueUp > maxUp) {
                    maxUp = valueUp;
                    visiblePoints.add(Point.of(x, y));
                }

                if (valueDown > maxDown) {
                    maxDown = valueDown;
                    visiblePoints.add(Point.of(x, maxY - y - 1));
                }

                if (valueLeft > maxLeft) {
                    maxLeft = valueLeft;
                    visiblePoints.add(Point.of(y, x));
                }

                if (valueRight > maxRight) {
                    maxRight = valueRight;
                    visiblePoints.add(Point.of(maxY - y - 1, x));
                }
            }
        }
    }

    private long computeViewingDistance(Point current, List<String> lines, Function<Point, Point> nextPointer) {
        int maxHeight = extractHeight(lines, current.x(), current.y());
        current = nextPointer.apply(current);
        int distance = 0;
        while (isValid(current)) {
            int height = extractHeight(lines, current.x(), current.y());
            distance++;
            if (height >= maxHeight) {
                break;
            }
            current = nextPointer.apply(current);
        }

        return distance;
    }

    private boolean isValid(Point current) {
        return current.x() >= 0 && current.y() >= 0 && current.x() < maxX && current.y() < maxY;
    }

    private static int extractHeight(List<String> lines, int x, int y) {
        return lines.get(x).charAt(y) - '0';
    }
}
