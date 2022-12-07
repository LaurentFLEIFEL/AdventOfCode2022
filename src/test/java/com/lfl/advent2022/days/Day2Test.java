package com.lfl.advent2022.days;

import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class Day2Test {

    @Test
    void test() {
        String line = """
                A Y
                B X
                C Z""";

        Day2 day2 = new Day2();

        day2.consume(line.lines().collect(Collectors.toList()));

        assertThat(day2.getTotalScore()).isEqualTo(15L);
        assertThat(day2.getTotalScore2()).isEqualTo(12L);
    }
}