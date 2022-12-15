package com.lfl.advent2022.days;

import com.lfl.advent2022.LinesConsumer;
import com.lfl.advent2022.utils.Point;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.collector.Collectors2;
import org.eclipse.collections.impl.list.Interval;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class Day15 implements LinesConsumer {

    private static final Pattern linePattern = Pattern.compile("Sensor at x=(?<x1>-?\\d+), y=(?<y1>-?\\d+): closest beacon is at x=(?<x2>-?\\d+), y=(?<y2>-?\\d+)");

    @Setter
    private int yToDetect = 2_000_000;

    @Setter
    private static int maxCoord = 4_000_000;

    @Override
    public void consume(List<String> lines) {
        MutableSet<Sensor> sensors = lines.stream()
                .map(Sensor::ofLine)
                .collect(Collectors2.toSet());

        log.info("Sensor size = {}", sensors.size());

        Intervals intervals = sensors.collect(sensor -> sensor.detectedAtY(yToDetect))
                .select(Optional::isPresent)
                .collect(Optional::get)
                .reduceInPlace(Intervals::empty, (Intervals::add));

        int beaconIncluded = sensors.collect(Sensor::getBeacon)
                .select(beacon -> beacon.y() == yToDetect)
                .select(beacon -> intervals.contains(beacon.x()))
                .size();


        log.info("x detected = {}", intervals.size() - beaconIncluded);

        MutableSet<Segment> edges = sensors.flatCollect(Sensor::getEdges);

        MutableSet<Point> eligiblePoints = edges.flatCollect(edge -> edges.reject(edge::equals)
                .collect(edge::intersectWith)
                .select(Optional::isPresent)
                .flatCollect(Optional::get)
        )
                .select(point -> point.x() >= 0)
                .select(point -> point.x() <= maxCoord)
                .select(point -> point.y() >= 0)
                .select(point -> point.y() <= maxCoord);

        MutableSet<Point> result = eligiblePoints.difference(sensors.collect(Sensor::getBeacon))
                .reject(point -> sensors.anySatisfy(sensor -> sensor.cover(point)));

        result.forEach(point -> {
            log.info("point = {}", point);
            log.info("result = {}", BigInteger.valueOf(point.x()).multiply(BigInteger.valueOf(4_000_000L)).add(BigInteger.valueOf(point.y())));
        });
    }

    private static class Intervals {
        @Getter
        private List<Interval> intervals = new ArrayList<>();

        public static Intervals empty() {
            return new Intervals();
        }

        public boolean contains(int i) {
            return intervals.stream().anyMatch(interval -> interval.contains(i));
        }

        public long size() {
            return intervals.stream().mapToInt(Interval::size).sum();
        }

        public void add(Interval interval) {
            intervals.add(interval);
            intervals.sort(Comparator.comparingInt(Interval::getFirst));

            int index = 0; // Stores index of last element
            // in output list

            // Traverse all input Intervals
            for (int i = 1; i < intervals.size(); i++) {
                Interval current = intervals.get(i);
                Interval previous = intervals.get(index);
                // If this is not first Interval and overlaps
                // with the previous one
                if (previous.getLast() >= current.getFirst() - 1) {
                    // Merge previous and current Intervals
                    previous = Interval.fromTo(previous.getFirst(), Math.max(previous.getLast(), current.getLast()));
                    intervals.set(index, previous);
                } else {
                    index++;
                    intervals.set(index, current);
                }
            }

            intervals = new ArrayList<>(intervals.subList(0, index + 1));
        }
    }

    @Data
    @Builder
    private static class Sensor {
        private Point sensor;
        private Point beacon;
        private int distance;
        private MutableSet<Segment> edges;

        public static Sensor ofLine(String line) {
            Matcher matcher = linePattern.matcher(line);
            matcher.find();

            Point sensorPosition = Point.of(Integer.parseInt(matcher.group("x1")), Integer.parseInt(matcher.group("y1")));
            Point beaconPosition = Point.of(Integer.parseInt(matcher.group("x2")), Integer.parseInt(matcher.group("y2")));
            int distance = sensorPosition.distance1(beaconPosition);
            Point vertex1 = Point.of(sensorPosition.x() - distance - 1, sensorPosition.y());
            Point vertex2 = Point.of(sensorPosition.x(), sensorPosition.y() + distance + 1);
            Point vertex3 = Point.of(sensorPosition.x() + distance + 1, sensorPosition.y());
            Point vertex4 = Point.of(sensorPosition.x(), sensorPosition.y() - distance - 1);
            return Sensor.builder()
                    .sensor(sensorPosition)
                    .beacon(beaconPosition)
                    .distance(distance)
                    .edges(Sets.mutable.of(
                            Segment.of(vertex1, vertex2),
                            Segment.of(vertex2, vertex3),
                            Segment.of(vertex3, vertex4),
                            Segment.of(vertex4, vertex1)
                    ))
                    .build();
        }

        public boolean cover(Point point) {
            return sensor.distance1(point) <= distance;
        }

        public Optional<Interval> detectedAtY(int y) {
            int deltaY = Math.abs(y - sensor.y());
            if (deltaY > distance) {
                return Optional.empty();
            }

            int width = distance - deltaY;
            int fromX = sensor.x() - width;
            int toX = sensor.x() + width;
            return Optional.of(Interval.fromTo(fromX, toX));
        }
    }

    private record Segment(Point start, Point end) {

        public static Segment of(Point start, Point end) {
            if (start.x() <= end.x()) {
                return new Segment(start, end);
            }
            return new Segment(end, start);
        }

        public boolean isWithin(Point point) {
            return start.x() <= point.x() && point.x() <= end.x() && start.y() <= point.y() && point.y() <= end.y();
        }

        public int slope() {
            return (end.y() - start.y()) / (end.x() - start.x());// only 1 or -1 possible
        }

        public int y0() {
            return end.y() - end.x() * this.slope();
        }

        public boolean doIntersectWith(Segment other) {
            if (this.start.x() > other.start.x()) {
                return other.doIntersectWith(this);
            }
            return (this.slope() == other.slope() && this.y0() == other.y0() && this.isWithin(other.start))
                    || (this.slope() != other.slope() && this.start.x() <= intersectionWith(other).x() && intersectionWith(other).x() <= this.end.x());
        }

        public Optional<MutableSet<Point>> intersectWith(Segment other) {
            if (!this.doIntersectWith(other)) {
                return Optional.empty();
            }

            if (this.slope() == other.slope()) {
                Point current = other.start;
                MutableSet<Point> points = Sets.mutable.empty();
                while (current.equals(this.end)) {
                    points.add(current);
                    current = Point.of(current.x() + 1, current.y() + this.slope());
                }
                points.add(current);
                return Optional.of(points);
            }

            return Optional.of(Sets.mutable.of(this.intersectionWith(other)));
        }

        private Point intersectionWith(Segment other) {
            int x = (other.y0() - this.y0()) / (this.slope() - other.slope());
            return Point.of(x, this.slope() * x + this.y0());
        }
    }
}
