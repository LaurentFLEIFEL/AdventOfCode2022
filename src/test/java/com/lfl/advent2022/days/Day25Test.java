package com.lfl.advent2022.days;

import org.eclipse.collections.impl.collector.Collectors2;
import org.junit.jupiter.api.Test;

class Day25Test {
    @Test
    void test() {
        String lines = """
                1=-0-2
                12111
                2=0=
                21
                2=01
                111
                20012
                112
                1=-1=
                1-12
                12
                1=
                122""";

        Day25 day25 = new Day25();
        day25.consume(lines.lines().collect(Collectors2.toList()));
    }
}