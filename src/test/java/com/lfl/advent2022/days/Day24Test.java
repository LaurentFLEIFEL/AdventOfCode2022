package com.lfl.advent2022.days;

import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

class Day24Test {
    @Test
    void test() {
        String lines = """
                """;

        Day24 day24 = new Day24();
        day24.consume(lines.lines().collect(Collectors.toList()));
    }
}