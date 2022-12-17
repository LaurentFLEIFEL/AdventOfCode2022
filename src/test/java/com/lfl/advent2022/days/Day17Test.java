package com.lfl.advent2022.days;

import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

class Day17Test {

    @Test
    void test() {
        String lines = """
                >>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>""";

        Day17 day17 = new Day17();
        Day17.setMaxSize(10);

        day17.consume(lines.lines().collect(Collectors.toList()));
    }
}