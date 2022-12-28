package com.lfl.advent2022.days;

import org.eclipse.collections.impl.collector.Collectors2;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Day3Test {

    @Test
    void test() {
        String line = """
                vJrwpWtwJgWrhcsFMMfFFhFp
                jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
                PmmdzqPrVvPwwTWBwg
                wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
                ttgJtRGJQctTZtZT
                CrZsJsPPZsGzwwsLwLmpwMDw""";

        Day3 day3 = new Day3();

        day3.consume(line.lines().collect(Collectors2.toList()));

        assertThat(day3.getSum1()).isEqualTo(157L);
        assertThat(day3.getSum2()).isEqualTo(70L);
    }

    @Test
    void name() {
        String s = "abcdefghijklmnopqrstuvwxyz";
        s = s + s.toUpperCase();

        System.out.println("s = " + s);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            System.out.println("c = " + c + " (" + priorityOf(c) + ")");
        }
    }

    private static int priorityOf(char c) {
        int resultLower = c - 'a' + 1;
        return resultLower > 0 ? resultLower : c - 'A' + 27;
    }
}