package com.lfl.advent2022.days;

import org.eclipse.collections.impl.collector.Collectors2;
import org.junit.jupiter.api.Test;

class Day22Test {
    @Test
    void test() {
        String lines = """
                        ...#
                        .#..
                        #...
                        ....
                ...#.......#
                ........#...
                ..#....#....
                ..........#.
                        ...#....
                        .....#..
                        .#......
                        ......#.
                                
                10R5L5R10L4R5L5""";

        Day22 day22 = new Day22();
        Day22.setEdgeSize(4);
        day22.consume(lines.lines().collect(Collectors2.toList()));
    }
}