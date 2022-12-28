package com.lfl.advent2022.days;

import org.eclipse.collections.impl.collector.Collectors2;
import org.junit.jupiter.api.Test;

class Day24Test {
    @Test
    void test() {
        String lines = """
                #.######
                #>>.<^<#
                #.<..<<#
                #>v.><>#
                #<^v^^>#
                ######.#""";

        Day24 day24 = new Day24();
        day24.consume(lines.lines().collect(Collectors2.toList()));
    }

    @Test
    void test2() {
        String lines = """
                #.#####
                #.....#
                #>....#
                #.....#
                #...v.#
                #.....#
                #####.#""";

        Day24 day24 = new Day24();
        day24.consume(lines.lines().collect(Collectors2.toList()));
    }
}