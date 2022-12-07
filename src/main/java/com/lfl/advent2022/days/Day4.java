package com.lfl.advent2022.days;

import com.lfl.advent2022.LinesConsumer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.collector.Collectors2;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.tuple.Tuples;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class Day4 implements LinesConsumer {
    private static final Pattern PATTERN = Pattern.compile("(?<from1>\\d+)-(?<to1>\\d+),(?<from2>\\d+)-(?<to2>\\d+)");
    @Getter
    private int size;
    @Getter
    private int size2;

    @Override
    public void consume(List<String> lines) {
        MutableList<Pair<Interval, Interval>> assignments = lines.stream()
                .map(Day4::lineToPair)
                .collect(Collectors2.toList());

        size = assignments.select(pair -> fullyContains(pair.getOne(), pair.getTwo()))
                .size();

        log.info("size = {}", size);

        size2 = assignments.select(pair -> overlap(pair.getOne(), pair.getTwo()))
                .size();

        log.info("size2 = {}", size2);
    }

    private static Pair<Interval, Interval> lineToPair(String line) {
        Matcher matcher = PATTERN.matcher(line);
        matcher.find();

        int from1 = Integer.parseInt(matcher.group("from1"));
        int to1 = Integer.parseInt(matcher.group("to1"));
        int from2 = Integer.parseInt(matcher.group("from2"));
        int to2 = Integer.parseInt(matcher.group("to2"));
        return Tuples.pair(Interval.fromTo(from1, to1), Interval.fromTo(from2, to2));
    }

    private static boolean fullyContains(Interval section1, Interval section2) {
        return section1.containsAll(section2) || section2.containsAll(section1);
    }

    private static boolean overlap(Interval section1, Interval section2) {
        return !section1.containsNone(section2);
    }
}
