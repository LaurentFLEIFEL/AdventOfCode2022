package com.lfl.advent2022.days;

import com.lfl.advent2022.LinesConsumer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.MutableCharList;
import org.eclipse.collections.api.set.primitive.MutableCharSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.collector.Collectors2;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Multimaps;
import org.eclipse.collections.impl.factory.primitive.CharLists;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Day3 implements LinesConsumer {

    @Getter
    private long sum1;
    @Getter
    private long sum2;

    @Override
    public void consume(MutableList<String> lines) {
        MutableList<Rucksack> rucksacks = lines.stream()
                .map(Rucksack::of)
                .collect(Collectors2.toList());

        sum1 = rucksacks.collectChar(Rucksack::detectDuplicate)
                .collectInt(Day3::priorityOf, IntLists.mutable.empty())
                .sum();
        log.info("sum1 = {}", sum1);

        sum2 = rucksacks.zipWithIndex()
                .groupByAndCollect(pair -> pair.getTwo() / 3, Pair::getOne, Multimaps.mutable.list.empty())
                .toMap(Lists.mutable::empty)
                .collectChar(list -> detectDuplicate(list.get(0), list.get(1), list.get(2)))
                .collectInt(Day3::priorityOf, IntLists.mutable.empty())
                .sum();
        log.info("sum2 = {}", sum2);
    }

    private record Rucksack(MutableCharList items) {
        public static Rucksack of(String line) {
            MutableCharList items = line.chars()
                    .collect(CharLists.mutable::empty,
                            (list, c) -> list.add((char) c),
                            MutableCharList::withAll);
            return new Rucksack(items);
        }

        public char detectDuplicate() {
            MutableCharSet firstPart = items.selectWithIndex((c, index) -> index < (items.size() / 2)).toSet();
            MutableCharSet secondPart = items.selectWithIndex((c, index) -> index >= (items.size() / 2)).toSet();

            return firstPart.intersect(secondPart).max();
        }
    }

    public char detectDuplicate(Rucksack rucksack1, Rucksack rucksack2, Rucksack rucksack3) {
        return rucksack1.items().toSet().intersect(rucksack2.items().toSet()).intersect(rucksack3.items().toSet()).max();
    }

    private static int priorityOf(char c) {
        int resultLower = c - 'a' + 1;
        return resultLower > 0 ? resultLower : c - 'A' + 27;
    }
}
