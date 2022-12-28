package com.lfl.advent2022.days;

import org.eclipse.collections.impl.collector.Collectors2;
import org.junit.jupiter.api.Test;

class Day14Test {

    @Test
    void test() {
        String lines = """
                498,4 -> 498,6 -> 496,6
                503,4 -> 502,4 -> 502,9 -> 494,9""";

        Day14 day14 = new Day14();

        day14.consume(lines.lines().collect(Collectors2.toList()));
    }
}