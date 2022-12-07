package com.lfl.advent2022.days;

import com.lfl.advent2022.LinesConsumer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.collector.Collectors2;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class Day2 implements LinesConsumer {

    @Getter
    private long totalScore;
    @Getter
    private long totalScore2;

    @Override
    public void consume(List<String> lines) {
        MutableList<Round> rounds = lines.stream()
                .map(Round::of)
                .collect(Collectors2.toList());

        totalScore = rounds.collectInt(Round::totalScore).sum();
        log.info("totalScore = {}", totalScore);

        totalScore2 = rounds.collectInt(Round::totalScore2).sum();
        log.info("totalScore2 = {}", totalScore2);
    }

    public record Round(Shape first, Shape second, Shape otherWay) {

        public static Round of(String line) {
            Shape first = Shape.ofFirst(line.charAt(0));
            Shape second = Shape.ofSecond(line.charAt(2));
            int otherShift = second.ordinal() - 1;
            Shape otherWay = Shape.values()[(first.ordinal() + otherShift + 3) % 3];
            return new Round(first, second, otherWay);
        }

        public int scoreOutcome() {
            return ((second.ordinal() - first.ordinal() + 3 + 1) % 3) * 3;
        }

        public int totalScore() {
            return second.ordinal() + 1 + this.scoreOutcome();
        }

        public int scoreOutcome2() {
            return ((otherWay.ordinal() - first.ordinal() + 3 + 1) % 3) * 3;
        }

        public int totalScore2() {
            return otherWay.ordinal() + 1 + this.scoreOutcome2();
        }
    }

    public enum Shape {
        ROCK('A', 'X'),
        PAPER('B', 'Y'),
        SCISSOR('C', 'Z');

        private final char first;
        private final char second;

        Shape(char first, char second) {
            this.first = first;
            this.second = second;
        }

        public static Shape ofFirst(char s) {
            return Arrays.stream(Shape.values())
                    .filter(shape -> shape.first == s)
                    .findAny()
                    .get();
        }

        public static Shape ofSecond(char s) {
            return Arrays.stream(Shape.values())
                    .filter(shape -> shape.second == s)
                    .findAny()
                    .get();
        }
    }
}
