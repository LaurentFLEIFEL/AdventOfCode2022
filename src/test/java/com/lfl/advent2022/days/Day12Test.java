package com.lfl.advent2022.days;

import org.eclipse.collections.impl.collector.Collectors2;
import org.junit.jupiter.api.Test;

class Day12Test {
    @Test
    void test() {
        String lines = """
                Sabqponm
                abcryxxl
                accszExk
                acctuvwj
                abdefghi""";

        Day12 day12 = new Day12();

        day12.consume(lines.lines().collect(Collectors2.toList()));
    }
}