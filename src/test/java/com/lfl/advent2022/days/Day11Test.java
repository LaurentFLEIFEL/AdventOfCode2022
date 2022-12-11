package com.lfl.advent2022.days;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

class Day11Test {

    @Test
    void test1() {
        MutableList<Day11.Monkey> monkeys = getTestMonkeys();
        Day11 day11 = new Day11();
        day11.setMonkeys(monkeys);

        day11.doPart1();
    }

    @Test
    void test2() {
        MutableList<Day11.Monkey> monkeys = getTestMonkeys();
        Day11 day11 = new Day11();
        day11.setMonkeys(monkeys);

        day11.doPart2();
    }

    private static MutableList<Day11.Monkey> getTestMonkeys() {
        return Lists.mutable.of(
                Day11.Monkey.builder()
                        .items(Lists.mutable.of(BigInteger.valueOf(79L), BigInteger.valueOf(98L)))
                        .operation(i -> i.multiply(BigInteger.valueOf(19L)))
                        .divisibleTest(BigInteger.valueOf(23L))
                        .targetIfTrue(2)
                        .targetIfFalse(3)
                        .build(),
                Day11.Monkey.builder()
                        .items(Lists.mutable.of(BigInteger.valueOf(54L), BigInteger.valueOf(65L), BigInteger.valueOf(75L), BigInteger.valueOf(74L)))
                        .operation(i -> i.add(BigInteger.valueOf(6L)))
                        .divisibleTest(BigInteger.valueOf(19L))
                        .targetIfTrue(2)
                        .targetIfFalse(0)
                        .build(),
                Day11.Monkey.builder()
                        .items(Lists.mutable.of(BigInteger.valueOf(79L), BigInteger.valueOf(60L), BigInteger.valueOf(97L)))
                        .operation(i -> i.multiply(i))
                        .divisibleTest(BigInteger.valueOf(13L))
                        .targetIfTrue(1)
                        .targetIfFalse(3)
                        .build(),
                Day11.Monkey.builder()
                        .items(Lists.mutable.of(BigInteger.valueOf(74L)))
                        .operation(i -> i.add(BigInteger.valueOf(3L)))
                        .divisibleTest(BigInteger.valueOf(17L))
                        .targetIfTrue(0)
                        .targetIfFalse(1)
                        .build()
        );
    }

    @Test
    void realInput1() {
        MutableList<Day11.Monkey> monkeys = buildRealInputs();
        Day11 day11 = new Day11();
        day11.setMonkeys(monkeys);

        day11.doPart1();
    }

    @Test
    void realInput2() {
        MutableList<Day11.Monkey> monkeys = buildRealInputs();
        Day11 day11 = new Day11();
        day11.setMonkeys(monkeys);

        day11.doPart2();
    }

    private static MutableList<Day11.Monkey> buildRealInputs() {
        return Lists.mutable.of(
                Day11.Monkey.builder()
                        .items(Lists.mutable.of(BigInteger.valueOf(72L), BigInteger.valueOf(97L)))
                        .operation(i -> i.multiply(BigInteger.valueOf(13L)))
                        .divisibleTest(BigInteger.valueOf(19L))
                        .targetIfTrue(5)
                        .targetIfFalse(6)
                        .build(),
                Day11.Monkey.builder()
                        .items(Lists.mutable.of(BigInteger.valueOf(55L), BigInteger.valueOf(70L), BigInteger.valueOf(90L), BigInteger.valueOf(74L), BigInteger.valueOf(95L)))
                        .operation(i -> i.multiply(i))
                        .divisibleTest(BigInteger.valueOf(7L))
                        .targetIfTrue(5)
                        .targetIfFalse(0)
                        .build(),
                Day11.Monkey.builder()
                        .items(Lists.mutable.of(BigInteger.valueOf(74L), BigInteger.valueOf(97L), BigInteger.valueOf(66L), BigInteger.valueOf(57L)))
                        .operation(i -> i.add(BigInteger.valueOf(6L)))
                        .divisibleTest(BigInteger.valueOf(17L))
                        .targetIfTrue(1)
                        .targetIfFalse(0)
                        .build(),
                Day11.Monkey.builder()
                        .items(Lists.mutable.of(BigInteger.valueOf(86L), BigInteger.valueOf(54L), BigInteger.valueOf(53L)))
                        .operation(i -> i.add(BigInteger.valueOf(2L)))
                        .divisibleTest(BigInteger.valueOf(13L))
                        .targetIfTrue(1)
                        .targetIfFalse(2)
                        .build(),
                Day11.Monkey.builder()
                        .items(Lists.mutable.of(BigInteger.valueOf(50L), BigInteger.valueOf(65L), BigInteger.valueOf(78L), BigInteger.valueOf(50L), BigInteger.valueOf(62L), BigInteger.valueOf(99L)))
                        .operation(i -> i.add(BigInteger.valueOf(3L)))
                        .divisibleTest(BigInteger.valueOf(11L))
                        .targetIfTrue(3)
                        .targetIfFalse(7)
                        .build(),
                Day11.Monkey.builder()
                        .items(Lists.mutable.of(BigInteger.valueOf(90L)))
                        .operation(i -> i.add(BigInteger.valueOf(4L)))
                        .divisibleTest(BigInteger.valueOf(2L))
                        .targetIfTrue(4)
                        .targetIfFalse(6)
                        .build(),
                Day11.Monkey.builder()
                        .items(Lists.mutable.of(BigInteger.valueOf(88L), BigInteger.valueOf(92L), BigInteger.valueOf(63L), BigInteger.valueOf(94L), BigInteger.valueOf(96L), BigInteger.valueOf(82L), BigInteger.valueOf(53L), BigInteger.valueOf(53L)))
                        .operation(i -> i.add(BigInteger.valueOf(8L)))
                        .divisibleTest(BigInteger.valueOf(5L))
                        .targetIfTrue(4)
                        .targetIfFalse(7)
                        .build(),
                Day11.Monkey.builder()
                        .items(Lists.mutable.of(BigInteger.valueOf(70L), BigInteger.valueOf(60L), BigInteger.valueOf(71L), BigInteger.valueOf(69L), BigInteger.valueOf(77L), BigInteger.valueOf(70L), BigInteger.valueOf(98L)))
                        .operation(i -> i.multiply(BigInteger.valueOf(7L)))
                        .divisibleTest(BigInteger.valueOf(3L))
                        .targetIfTrue(2)
                        .targetIfFalse(3)
                        .build()
        );
    }
}