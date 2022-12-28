package com.lfl.advent2022.days;

import org.eclipse.collections.impl.collector.Collectors2;
import org.junit.jupiter.api.Test;

class Day8Test {

    @Test
    void test() {
        String lines = """
                30373
                25512
                65332
                33549
                35390""";

        Day8 day8 = new Day8();

        day8.consume(lines.lines().collect(Collectors2.toList()));
    }
}