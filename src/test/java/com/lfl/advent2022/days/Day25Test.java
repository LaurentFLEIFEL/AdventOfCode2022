package com.lfl.advent2022.days;

import org.eclipse.collections.impl.collector.Collectors2;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

class Day25Test {
    @Test
    void test() {
        String lines = """
                """;

        Day25 day25 = new Day25();
        day25.consume(lines.lines().collect(Collectors2.toList()));
    }
}