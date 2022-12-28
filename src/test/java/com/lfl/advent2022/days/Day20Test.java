package com.lfl.advent2022.days;

import org.eclipse.collections.impl.collector.Collectors2;
import org.junit.jupiter.api.Test;

class Day20Test {

    @Test
    void test() {
        String lines = """
                1
                2
                -3
                3
                -2
                0
                4""";

        Day20 day20 = new Day20();

        day20.consume(lines.lines().collect(Collectors2.toList()));
    }
}