package com.lfl.advent2022.days;

import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class Day5Test {
    @Test
    void test() {
        String lines = """
                    [D]
                [N] [C]
                [Z] [M] [P]
                 1   2   3
                                
                move 1 from 2 to 1
                move 3 from 1 to 3
                move 2 from 2 to 1
                move 1 from 1 to 2""";

        Day5 day5 = new Day5();

        day5.setFirstPart(true);
        day5.consume(lines.lines().collect(Collectors.toList()));

        assertThat(day5.getMessage()).isEqualTo("CMZ");
    }

    @Test
    void test2() {
        String lines = """
                    [D]
                [N] [C]
                [Z] [M] [P]
                 1   2   3
                                
                move 1 from 2 to 1
                move 3 from 1 to 3
                move 2 from 2 to 1
                move 1 from 1 to 2""";

        Day5 day5 = new Day5();

        day5.setFirstPart(false);
        day5.consume(lines.lines().collect(Collectors.toList()));

        assertThat(day5.getMessage()).isEqualTo("MCD");
    }
}