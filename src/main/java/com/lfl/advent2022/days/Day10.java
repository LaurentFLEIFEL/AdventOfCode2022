package com.lfl.advent2022.days;

import com.lfl.advent2022.LinesConsumer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.factory.primitive.IntSets;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.primitive.ImmutableIntSet;
import org.eclipse.collections.impl.collector.Collectors2;
import org.eclipse.collections.impl.factory.Lists;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.function.BiFunction;

@Slf4j
@Service
public class Day10 implements LinesConsumer {
    @Override
    public void consume(MutableList<String> lines) {
        MutableList<Command> commands = lines.stream()
                .map(Command::of)
                .collect(Collectors2.toList());

        long sum = part1(commands);
        log.info("sum = {}", sum);

        MutableList<MutableList<String>> screen = part2(commands);
        screen.forEach(row -> log.info(row.makeString("")));
    }

    private static MutableList<MutableList<String>> part2(MutableList<Command> commands) {
        Crt crt = Crt.create();
        MutableList<MutableList<String>> screen = Lists.mutable.empty();
        for (Command command : commands) {
            for (int i = 0; i < command.getCycleNbr(); i++) {
                crt.incrementCycle();

                if (crt.isNewLine()) {
                    screen.add(Lists.mutable.empty());
                }

                if (crt.isLit()) {
                    screen.getLast().add("#");
                } else {
                    screen.getLast().add(".");
                }
            }
            crt.apply(command);
        }
        return screen;
    }

    private static long part1(MutableList<Command> commands) {
        Crt crt = Crt.create();
        long sum = 0;

        for (Command command : commands) {
            for (int i = 0; i < command.getCycleNbr(); i++) {
                crt.incrementCycle();
                sum += crt.getInterestingSignalStrength();
            }
            crt.apply(command);
        }
        return sum;
    }

    private static class Crt {

        private static final ImmutableIntSet interestingCycles = IntSets.immutable.of(20, 60, 100, 140, 180, 220);

        @Setter
        private int x = 1;
        private int cycle = 0;

        public static Crt create() {
            return new Crt();
        }

        public boolean isLit() {
            return (cycle % 40) - 1 >= x - 1 && (cycle % 40) - 1 <= x + 1;
        }

        public boolean isNewLine() {
            return cycle % 40 == 1;
        }

        public void incrementCycle() {
            cycle++;
        }

        public boolean isInterestingCycle() {
            return interestingCycles.contains(cycle);
        }

        public int signalStrength() {
            return x * cycle;
        }

        public int getInterestingSignalStrength() {
            return isInterestingCycle() ? signalStrength() : 0;
        }

        public void apply(Command command) {
            x = command.apply(x);
        }
    }

    private record Command(Operation operation, String line) {
        public static Command of(String line) {
            return new Command(Operation.of(line), line);
        }

        public int apply(int x) {
            return operation.apply(x, line);
        }

        public int getCycleNbr() {
            return operation.getCycleNbr();
        }
    }

    public enum Operation {
        ADDX("addx", 2, (x, line) -> x + Integer.parseInt(line.substring(5))),
        NOOP("noop", 1, (x, line) -> x);

        private final String code;
        @Getter
        private final int cycleNbr;
        private final BiFunction<Integer, String, Integer> operation;

        Operation(String code, int cycleNbr, BiFunction<Integer, String, Integer> operation) {
            this.code = code;
            this.cycleNbr = cycleNbr;
            this.operation = operation;
        }

        public static Operation of(String line) {
            return Arrays.stream(Operation.values())
                    .filter(command -> line.startsWith(command.code))
                    .findAny()
                    .orElseThrow(IllegalArgumentException::new);
        }

        public int apply(int x, String line) {
            return operation.apply(x, line);
        }
    }
}
