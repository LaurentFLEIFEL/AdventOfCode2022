package com.lfl.advent2022.days;

import org.eclipse.collections.impl.collector.Collectors2;
import org.junit.jupiter.api.Test;

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
        day23.consume(lines.lines().collect(Collectors2.toList()));
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
        day23.consume(lines.lines().collect(Collectors2.toList()));
    }
}