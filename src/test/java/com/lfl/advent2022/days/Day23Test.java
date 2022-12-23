package com.lfl.advent2022.days;

import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

class Day23Test {

    @Test
    void test() {
        String lines = """
                .....
                ..##.
                ..#..
                .....
                ..##.
                .....""";

        Day23 day23 = new Day23();
        day23.consume(lines.lines().collect(Collectors.toList()));
    }

    @Test
    void test2() {
        String lines = """
                ....#..
                ..###.#
                #...#.#
                .#...##
                #.###..
                ##.#.##
                .#..#..""";

        Day23 day23 = new Day23();
        day23.consume(lines.lines().collect(Collectors.toList()));
    }
}