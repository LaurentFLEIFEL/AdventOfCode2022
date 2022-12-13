package com.lfl.advent2022.days;

import com.lfl.advent2022.LinesConsumer;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.collector.Collectors2;
import org.eclipse.collections.impl.factory.Lists;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

@Slf4j
@Service
public class Day13 implements LinesConsumer {
    @Override
    public void consume(List<String> lines) {
        MutableList<Packet> packets = lines.stream()
                .filter(line -> !line.isEmpty())
                .map(Packet::ofLine)
                .collect(Collectors2.toList());

        part1(lines, packets);

        part2(packets);
    }

    private static void part2(MutableList<Packet> packets) {
        Packet two = Packet.ofList(Lists.mutable.of(Packet.ofList(Lists.mutable.of(Packet.ofInt(2)))));
        Packet six = Packet.ofList(Lists.mutable.of(Packet.ofList(Lists.mutable.of(Packet.ofInt(6)))));
        MutableSet<Packet> dividers = Sets.mutable.of(two, six);
        packets.addAll(dividers);

        Collections.sort(packets);

        long product = packets.zipWithIndex()
                .select(pair -> dividers.contains(pair.getOne()))
                .collectInt(pair -> pair.getTwo() + 1)
                .reduce((acc, value) -> acc * value);

        log.info("product = {}", product);
    }

    private static void part1(List<String> lines, MutableList<Packet> packets) {
        MutableList<Packet> even = packets.selectWithIndex((packet, index) -> index % 2 == 0);
        MutableList<Packet> odd = packets.selectWithIndex((packet, index) -> index % 2 == 1);
        MutableList<Pair<Packet, Packet>> pairs = even.zip(odd);

        checkDataAreCorrects(lines, pairs);

        long sum = pairs.collect(pair -> areInRightOrder(pair.getOne(), pair.getTwo()))
                .collectWithIndex((result, index) -> result ? index + 1 : 0)
                .collectInt(i -> i)
                .sum();

        log.info("sum = {}", sum);
    }

    private static void checkDataAreCorrects(List<String> lines, MutableList<Pair<Packet, Packet>> pairs) {
        String s = pairs.makeString((pair) -> pair.getOne().toString() + "\n" + pair.getTwo().toString(), "", "\n\n", "");
        s = s.replaceAll(" ", "");
        log.info("Contents are identical = {}", s.equals(String.join("\n", lines)));
    }

    public static boolean areInRightOrder(Packet packet1, Packet packet2) {
        return packet1.compareTo(packet2) <= 0;
    }

    @Builder
    @Data
    private static class Packet implements Comparable<Packet> {
        private int value;
        private MutableList<Packet> list;

        public static Packet ofInt(int value) {
            return Packet.builder().value(value).build();
        }

        public static Packet ofList(MutableList<Packet> list) {
            return Packet.builder().list(list).build();
        }

        public static Packet ofList() {
            return Packet.builder().list(Lists.mutable.empty()).build();
        }

        public int compareTo(Packet packet2) {
            //log.info("Compare {} vs {}", packet1, packet2);
            if (this.isInteger() && packet2.isInteger()) {
                return this.getValue() - packet2.getValue();
            }

            if (this.isList() && packet2.isList()) {
                for (int index = 0; index < Math.min(this.getList().size(), packet2.getList().size()); index++) {
                    if (this.getList().get(index).compareTo(packet2.getList().get(index)) == 0) {
                        continue;
                    }
                    return this.getList().get(index).compareTo(packet2.getList().get(index));
                }

                return this.getList().size() - packet2.getList().size();
            }

            if (this.isInteger()) {
                //log.info("Mixed types; convert left to {} and retry comparison", packet1.intToList());
                return this.intToList().compareTo(packet2);
            }
            // log.info("Mixed types; convert right to {} and retry comparison", packet2.intToList());
            return this.compareTo(packet2.intToList());
        }

        public static Packet ofLine(String line) {
            Deque<Packet> stack = new ArrayDeque<>();
            int aux = -1;
            for (int index = 0; index < line.length(); index++) {
                char c = line.charAt(index);
                if (c == '[') {
                    stack.addLast(Packet.ofList());
                    continue;
                }

                if (c == ',' && aux != -1) {
                    stack.getLast().addToList(Packet.ofInt(aux));
                    aux = -1;
                    continue;
                }

                if (c == ',') {
                    continue;
                }

                if (c == ']' && aux != -1) {
                    stack.getLast().addToList(Packet.ofInt(aux));
                    aux = -1;
                }

                if (c == ']' && index != line.length() - 1) {
                    Packet packet = stack.pollLast();
                    stack.getLast().addToList(packet);
                    continue;
                }

                if (c == ']' && index == line.length() - 1) {
                    continue;
                }

                if (aux == -1) {
                    aux = c - '0';
                } else {
                    aux = 10 * aux + c - '0';
                }
            }
            return stack.getLast();
        }

        public void addToList(Packet packet) {
            if (isInteger()) {
                throw new UnsupportedOperationException();
            }

            list.add(packet);
        }

        public Packet intToList() {
            if (isList()) {
                throw new UnsupportedOperationException();
            }

            return Packet.ofList(Lists.mutable.of(Packet.ofInt(value)));
        }

        public boolean isInteger() {
            return list == null;
        }

        public boolean isList() {
            return !isInteger();
        }

        @Override
        public String toString() {
            if (isInteger()) {
                return Integer.toString(value);
            }
            return list.toString();
        }
    }
}
