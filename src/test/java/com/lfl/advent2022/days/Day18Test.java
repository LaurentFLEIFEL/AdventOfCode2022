package com.lfl.advent2022.days;

import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

class Day18Test {

    @Test
    void test() {
        String lines = """
                2,2,2
                1,2,2
                3,2,2
                2,1,2
                2,3,2
                2,2,1
                2,2,3
                2,2,4
                2,2,6
                1,2,5
                3,2,5
                2,1,5
                2,3,5""";

        Day18 day18 = new Day18();

        day18.consume(lines.lines().collect(Collectors.toList()));
    }

    @Test
    void test2() {
        String lines = """
                1,1,1
                2,1,1""";

        Day18 day18 = new Day18();

        day18.consume(lines.lines().collect(Collectors.toList()));
    }
}