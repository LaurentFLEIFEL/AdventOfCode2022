package com.lfl.advent2022.days;

import org.eclipse.collections.impl.collector.Collectors2;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Day4Test {

    @Test
    void test() {
        String lines = """
                2-4,6-8
                2-3,4-5
                5-7,7-9
                2-8,3-7
                6-6,4-6
                2-6,4-8""";

        Day4 day4 = new Day4();

        day4.consume(lines.lines().collect(Collectors2.toList()));

        assertThat(day4.getSize()).isEqualTo(2);
        assertThat(day4.getSize2()).isEqualTo(4);
    }
}