package com.lfl.advent2022.days;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Day16Test {


    @Test
    void test() {
        String lines = """
                Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
                Valve BB has flow rate=13; tunnels lead to valves CC, AA
                Valve CC has flow rate=2; tunnels lead to valves DD, BB
                Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE
                Valve EE has flow rate=3; tunnels lead to valves FF, DD
                Valve FF has flow rate=0; tunnels lead to valves EE, GG
                Valve GG has flow rate=0; tunnels lead to valves FF, HH
                Valve HH has flow rate=22; tunnel leads to valve GG
                Valve II has flow rate=0; tunnels lead to valves AA, JJ
                Valve JJ has flow rate=21; tunnel leads to valve II""";

        Day16 day16 = new Day16();

        day16.consume(lines.lines().collect(Collectors.toList()));
    }

    @Test
    void name() {
        Pattern pattern = Pattern.compile("Valve (?<id>[A-Z]+) has flow rate=(?<rate>\\d+); tunnels lead to valve(s?) (?<ids>.+)");

        String line = "Valve FF has flow rate=0; tunnels lead to valves EE, GG";
        Matcher matcher = pattern.matcher(line);
        matcher.find();

        String ids = matcher.group("ids");
        String id = matcher.group("id");
        int rate = Integer.parseInt(matcher.group("rate"));

        System.out.println("id = " + id);
        System.out.println("rate = " + rate);
        System.out.println("ids = " + ids);
    }
}