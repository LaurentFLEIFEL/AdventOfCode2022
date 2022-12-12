package com.lfl.advent2022.days;

import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

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

        day12.consume(lines.lines().collect(Collectors.toList()));
    }
}