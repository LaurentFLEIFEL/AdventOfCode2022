package com.lfl.advent2022.days;

import org.eclipse.collections.impl.collector.Collectors2;
import org.junit.jupiter.api.Test;

class Day13Test {

    @Test
    void test() {
        String lines = """
                [1,1,3,1,1]
                [1,1,5,1,1]
                                
                [[1],[2,3,4]]
                [[1],4]
                                
                [9]
                [[8,7,6]]
                                
                [[4,4],4,4]
                [[4,4],4,4,4]
                                
                [7,7,7,7]
                [7,7,7]
                                
                []
                [3]
                                
                [[[]]]
                [[]]
                                
                [1,[2,[3,[4,[5,6,7]]]],8,9]
                [1,[2,[3,[4,[5,6,0]]]],8,9]""";

        Day13 day13 = new Day13();

        day13.consume(lines.lines().collect(Collectors2.toList()));
    }
}