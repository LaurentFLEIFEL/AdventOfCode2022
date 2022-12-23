package com.lfl.advent2022.days;

import com.lfl.advent2022.LinesConsumer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.factory.primitive.IntLists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.impl.factory.Lists;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class Day1 implements LinesConsumer {
    @Override
    public void consume(MutableList<String> lines) {
        MutableList<ElfCarry> elfCarries = parseInput(lines);

        long max = elfCarries.collectLong(ElfCarry::total)
                .max();

        log.info("Max = {}", max);

        MutableLongList top3 = elfCarries.collectLong(ElfCarry::total)
                .sortThis()
                .selectWithIndex((n, index) -> index > elfCarries.size() - 4);
        log.info("Sum of top 3 = {}", top3.sum());
    }

    private static MutableList<ElfCarry> parseInput(List<String> lines) {
        MutableList<ElfCarry> elfCarries = Lists.mutable.empty();
        ElfCarry elfCarry = ElfCarry.create();

        for (String line : lines) {
            if (line.isEmpty()) {
                elfCarries.add(elfCarry);
                elfCarry = ElfCarry.create();
                continue;
            }
            elfCarry.add(Integer.parseInt(line));
        }
        elfCarries.add(elfCarry);
        return elfCarries;
    }

    @NoArgsConstructor(staticName = "create")
    private static class ElfCarry {
        @Getter
        private final MutableIntList snacks = IntLists.mutable.empty();

        public void add(int snackCalories) {
            snacks.add(snackCalories);
        }

        public long total() {
            return snacks.sum();
        }
    }
}
