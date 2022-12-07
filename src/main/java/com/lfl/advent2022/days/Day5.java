package com.lfl.advent2022.days;

import com.lfl.advent2022.LinesConsumer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.collector.Collectors2;
import org.eclipse.collections.impl.factory.Lists;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

@Slf4j
@Service
public class Day5 implements LinesConsumer {
    private static final Pattern PATTERN = Pattern.compile("move (?<quantity>\\d+) from (?<from>\\d+) to (?<to>\\d+)");
    @Getter
    private String message;
    @Setter
    private boolean isFirstPart = false;

    @Override
    public void consume(List<String> lines) {
        MutableList<Deque<Character>> stacks = extractStacks(lines);
        MutableList<Procedure> procedures = extractProcedures(lines);

        if (isFirstPart) {
            procedures.forEach(procedure -> procedure.applyCrateMover9000(stacks));
        } else {
            procedures.forEach(procedure -> procedure.applyCrateMover9001(stacks));
        }

        message = stacks.collect(Deque::pollFirst)
                .reject(Objects::isNull)
                .makeString("");

        log.info("message = {}", message);
    }

    private static MutableList<Deque<Character>> extractStacks(List<String> lines) {
        MutableList<Deque<Character>> stacks = Lists.mutable.withNValues(10, ArrayDeque::new);

        for (String line : lines) {
            int indexOf = line.indexOf('[');
            if (indexOf == -1) {
                break;
            }

            stacks.get(indexOf / 4).add(line.charAt(indexOf + 1));
            while ((indexOf = line.indexOf('[', indexOf + 1)) != -1) {
                stacks.get(indexOf / 4).add(line.charAt(indexOf + 1));
            }
        }
        return stacks;
    }

    private static MutableList<Procedure> extractProcedures(List<String> lines) {
        return lines.stream()
                .dropWhile(line -> !line.contains("move "))
                .map(Procedure::fromLine)
                .collect(Collectors2.toList());
    }

    private record Procedure(int quantity, int from, int to) {
        public static Procedure fromLine(String line) {
            Matcher matcher = PATTERN.matcher(line);
            matcher.find();

            return new Procedure(Integer.parseInt(matcher.group("quantity")), Integer.parseInt(matcher.group("from")), Integer.parseInt(matcher.group("to")));
        }

        public void applyCrateMover9000(List<Deque<Character>> stacks) {
            IntStream.range(0, quantity)
                    .forEach(i -> {
                        Character c = stacks.get(from - 1).removeFirst();
                        stacks.get(to - 1).addFirst(c);
                    });
        }

        public void applyCrateMover9001(List<Deque<Character>> stacks) {
            ArrayDeque<Character> aux = new ArrayDeque<>();
            IntStream.range(0, quantity)
                    .forEach(i -> {
                        Character c = stacks.get(from - 1).removeFirst();
                        aux.addFirst(c);
                    });

            IntStream.range(0, quantity)
                    .forEach(i -> {
                        Character c = aux.removeFirst();
                        stacks.get(to - 1).addFirst(c);
                    });
        }
    }
}
