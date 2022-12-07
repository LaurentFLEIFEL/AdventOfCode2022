package com.lfl.advent2022.days;

import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class Day7Test {

    @Test
    void test() {
        String lines = """
                $ cd /
                $ ls
                dir a
                14848514 b.txt
                8504156 c.dat
                dir d
                $ cd a
                $ ls
                dir e
                29116 f
                2557 g
                62596 h.lst
                $ cd e
                $ ls
                584 i
                $ cd ..
                $ cd ..
                $ cd d
                $ ls
                4060174 j
                8033020 d.log
                5626152 d.ext
                7214296 k""";

        Day7 day7 = new Day7();

        day7.consume(lines.lines().collect(Collectors.toList()));

        assertThat(day7.getSum()).isEqualTo(95437);
        assertThat(day7.getSize()).isEqualTo(24933642);
    }
}