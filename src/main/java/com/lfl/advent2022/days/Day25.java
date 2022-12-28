package com.lfl.advent2022.days;

import com.lfl.advent2022.LinesConsumer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.utility.ArrayIterate;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.function.BiConsumer;

@Slf4j
public class Day25 implements LinesConsumer {
    @Override
    public void consume(MutableList<String> lines) {
        BigInteger result = lines.collect(Day25::fromSnafu)
                .reduce(BigInteger::add)
                .get();

        log.info("result = {}", result);

        String result2 = toSnafu(result);
        log.info("result2 = {}", result2);
    }

    private static BigInteger fromSnafu(String line) {
        return ArrayIterate.collect(line.split(""), s -> s)
                .reverseThis()
                .collect(SnafuNumber::of)
                .collectWithIndex((snafuNumber, index) -> snafuNumber.value().multiply(BigInteger.valueOf(5).pow(index)))
                .reduce(BigInteger::add)
                .get();
    }

    private static String toSnafu(BigInteger value) {
        MutableList<Integer> digits = Lists.mutable.withNValues(30, () -> 0);

        int index = 0;
        BigInteger five = BigInteger.valueOf(5);
        while (!value.equals(BigInteger.ZERO)) {
            int ordinal = value.mod(five).intValue();
            SnafuNumber.values()[ordinal].add(digits, index);
            if (digits.get(index) == 3) {
                digits.set(index + 1, digits.get(index + 1) + 1);
                digits.set(index, -2);
            } else if (digits.get(index) == 4) {
                digits.set(index + 1, digits.get(index + 1) + 1);
                digits.set(index, -1);
            }
            value = value.add(BigInteger.valueOf(ordinal).negate()).divide(five);
            index++;
        }

        return digits.subList(0, index + 1).reverseThis()
                .dropWhile(i -> i == 0)
                .collect(SnafuNumber::of)
                .collect(SnafuNumber::getCode)
                .makeString("");
    }

    enum SnafuNumber {
        ZERO("0", BigInteger.ZERO, (list, index) -> {
        }, 0),
        ONE("1", BigInteger.ONE, (list, index) -> list.set(index, list.get(index) + 1), 1),
        TWO("2", BigInteger.TWO, (list, index) -> list.set(index, list.get(index) + 2), 2),
        DOUBLE_MINUS("=", BigInteger.TWO.negate(), (list, index) -> {
            list.set(index + 1, list.get(index + 1) + 1);
            list.set(index, list.get(index) - 2);
        }, -2),
        MINUS("-", BigInteger.ONE.negate(), (list, index) -> {
            list.set(index + 1, list.get(index + 1) + 1);
            list.set(index, list.get(index) - 1);
        }, -1);

        @Getter
        private final String code;
        private final BigInteger value;
        private final BiConsumer<MutableList<Integer>, Integer> adder;
        private final int number;

        SnafuNumber(String code, BigInteger value, BiConsumer<MutableList<Integer>, Integer> adder, int number) {
            this.code = code;
            this.value = value;
            this.adder = adder;
            this.number = number;
        }

        public static SnafuNumber of(String code) {
            return Arrays.stream(SnafuNumber.values())
                    .filter(snafuNumber -> snafuNumber.code.equals(code))
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException(code));
        }

        public static SnafuNumber of(int number) {
            return Arrays.stream(SnafuNumber.values())
                    .filter(snafuNumber -> snafuNumber.number == number)
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException("" + number));
        }

        public BigInteger value() {
            return this.value;
        }

        public void add(MutableList<Integer> list, int index) {
            this.adder.accept(list, index);
        }
    }
}
