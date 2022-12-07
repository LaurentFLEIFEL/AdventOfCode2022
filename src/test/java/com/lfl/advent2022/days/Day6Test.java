package com.lfl.advent2022.days;

import org.assertj.core.api.Assertions;
import org.eclipse.collections.impl.factory.Lists;
import org.junit.jupiter.api.Test;

class Day6Test {

    @Test
    void test1() {
        String line = "mjqjpqmgbljsphdztnvjfqwrcgsmlb";

        Day6 day6 = new Day6();
        day6.consume(Lists.mutable.of(line));

        Assertions.assertThat(day6.getStartOfPacket()).isEqualTo(7);
        Assertions.assertThat(day6.getStartOfMessage()).isEqualTo(19);
    }

    @Test
    void test2() {
        String line = "bvwbjplbgvbhsrlpgdmjqwftvncz";

        Day6 day6 = new Day6();
        day6.consume(Lists.mutable.of(line));

        Assertions.assertThat(day6.getStartOfPacket()).isEqualTo(5);
        Assertions.assertThat(day6.getStartOfMessage()).isEqualTo(23);
    }

    @Test
    void test3() {
        String line = "nppdvjthqldpwncqszvftbrmjlhg";

        Day6 day6 = new Day6();
        day6.consume(Lists.mutable.of(line));

        Assertions.assertThat(day6.getStartOfPacket()).isEqualTo(6);
        Assertions.assertThat(day6.getStartOfMessage()).isEqualTo(23);
    }

    @Test
    void test4() {
        String line = "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg";

        Day6 day6 = new Day6();
        day6.consume(Lists.mutable.of(line));

        Assertions.assertThat(day6.getStartOfPacket()).isEqualTo(10);
        Assertions.assertThat(day6.getStartOfMessage()).isEqualTo(29);
    }

    @Test
    void test5() {
        String line = "zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw";

        Day6 day6 = new Day6();
        day6.consume(Lists.mutable.of(line));

        Assertions.assertThat(day6.getStartOfPacket()).isEqualTo(11);
        Assertions.assertThat(day6.getStartOfMessage()).isEqualTo(26);
    }
}