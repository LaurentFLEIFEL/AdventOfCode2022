package com.lfl.advent2022.days;

import com.lfl.advent2022.LinesConsumer;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.partition.list.PartitionMutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

@Slf4j
@Service
public class Day11 implements LinesConsumer {
    @Setter
    private MutableList<Monkey> monkeys = Lists.mutable.empty();
    private static BigInteger lcm;

    @Override
    public void consume(MutableList<String> lines) {
    }

    public void doPart1() {
        computeMonkeyBusiness(20, monkey -> monkey.doTurnPart1(monkeys));
    }

    public void doPart2() {
        lcm = monkeys.collect(Monkey::getDivisibleTest)
                .reduce(Day11::lcm)
                .orElseThrow(IllegalArgumentException::new);

        computeMonkeyBusiness(10_000, monkey -> monkey.doTurnPart2(monkeys));
    }

    private void computeMonkeyBusiness(int roundTotal, Procedure<Monkey> monkeyTurnProcedure) {
        IntStream.range(0, roundTotal)
                .forEach(round -> monkeys.forEach(monkeyTurnProcedure));

        MutableIntList top2 = monkeys.collectInt(Monkey::getNbrInspected)
                .sortThis()
                .selectWithIndex((n, index) -> index > monkeys.size() - 3);

        log.info("product = {}", top2.reduce((a, b) -> a * b));
    }

    private static BigInteger lcm(BigInteger number1, BigInteger number2) {
        BigInteger gcd = number1.gcd(number2);
        BigInteger absProduct = number1.multiply(number2).abs();
        return absProduct.divide(gcd);
    }

    @Data
    @Builder
    public static class Monkey {
        private static final BigInteger THREE = BigInteger.valueOf(3L);
        private MutableList<BigInteger> items;
        private UnaryOperator<BigInteger> operation;
        private BigInteger divisibleTest;
        private int targetIfTrue;
        private int targetIfFalse;
        private int nbrInspected;

        public void doTurnPart1(MutableList<Monkey> monkeys) {
            doTurn(monkeys, itemWorry -> itemWorry.divide(THREE));
        }

        public void doTurnPart2(MutableList<Monkey> monkeys) {
            doTurn(monkeys, itemWorry -> itemWorry.mod(lcm));
        }

        private void doTurn(MutableList<Monkey> monkeys, Function<BigInteger, BigInteger> smallizer) {
            PartitionMutableList<BigInteger> partition = items.collect(itemWorry -> operation.apply(itemWorry))
                    .collect(smallizer)
                    .partition(itemWorry -> itemWorry.mod(divisibleTest).equals(BigInteger.ZERO));
            monkeys.get(targetIfTrue).getItems().addAll(partition.getSelected());
            monkeys.get(targetIfFalse).getItems().addAll(partition.getRejected());
            nbrInspected += items.size();
            items = Lists.mutable.empty();
        }
    }
}
