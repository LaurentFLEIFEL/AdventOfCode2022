package com.lfl.advent2022.days;

import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

class Day9Test {
    @Test
    void test() {
        String lines = """
                R 4
                U 4
                L 3
                D 1
                R 4
                D 1
                L 5
                R 2""";

        Day9 day9 = new Day9();

        day9.consume(lines.lines().collect(Collectors.toList()));

        //assertThat(day9.getSum()).isEqualTo(95437);
    }

    @Test
    void test2() {
        String lines = """
                R 5
                U 8
                L 8
                D 3
                R 17
                D 10
                L 25
                U 20""";

        Day9 day9 = new Day9();

        day9.consume(lines.lines().collect(Collectors.toList()));

        //assertThat(day9.getSum()).isEqualTo(95437);
    }
}