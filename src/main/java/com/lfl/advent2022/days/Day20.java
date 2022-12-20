package com.lfl.advent2022.days;

import com.lfl.advent2022.LinesConsumer;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.collector.Collectors2;
import org.eclipse.collections.impl.tuple.Tuples;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

@Slf4j
@Service
public class Day20 implements LinesConsumer {
    @Override
    public void consume(List<String> lines) {
        //use pair to keep original index because one number can appear several times - wtf
        MutableList<Pair<BigInteger, Integer>> encrypted = lines.stream()
                .map(line -> new BigInteger(line, 10))
                .collect(Collectors2.toList())
                .collectWithIndex(Tuples::pair);

        MutableList<Pair<BigInteger, Integer>> mixed = mix(Lists.mutable.withAll(encrypted), encrypted);

        printResult(encrypted, mixed);

        BigInteger decryptionKey = BigInteger.valueOf(811589153L);
        MutableList<Pair<BigInteger, Integer>> withKey = encrypted.collect(pair -> Tuples.pair(pair.getOne().multiply(decryptionKey), pair.getTwo()));
        MutableList<Pair<BigInteger, Integer>> toMix = Lists.mutable.withAll(withKey);
        IntStream.range(0, 10).forEach(i -> mix(toMix, withKey));

        printResult(withKey, toMix);
    }

    private static void printResult(MutableList<Pair<BigInteger, Integer>> encrypted, MutableList<Pair<BigInteger, Integer>> list) {
        Pair<BigInteger, Integer> zero = encrypted.stream()
                .filter(pair -> pair.getOne().equals(BigInteger.ZERO))
                .findAny()
                .get();
        int indexOf0 = list.indexOf(zero);
        log.info("index of 0 = {}", indexOf0);
        while (indexOf0 + 3000 >= list.size()) {
            list.addAll(list.stream().toList());
        }

        BigInteger n1000 = list.get(indexOf0 + 1000).getOne();
        BigInteger n2000 = list.get(indexOf0 + 2000).getOne();
        BigInteger n3000 = list.get(indexOf0 + 3000).getOne();
        log.info("1000th = {}, 2000th = {}, 3000th = {}", n1000, n2000, n3000);
        log.info("sum = {}", n1000.add(n2000.add(n3000)));
    }

    public static MutableList<Pair<BigInteger, Integer>> mix(MutableList<Pair<BigInteger, Integer>> toMix, MutableList<Pair<BigInteger, Integer>> encrypted) {
        encrypted.forEach(n -> move(n, toMix, Pair::getOne));
        //log.info("list = {}", list);
        return toMix;
    }

    public static <T> List<T> move(T n, List<T> list, Function<T, BigInteger> extractor) {
        // log.info("list = {}", list);
        int index = list.indexOf(n);
        BigInteger newIndex = extractor.apply(n).add(BigInteger.valueOf(index)).mod(BigInteger.valueOf(list.size() - 1));
        list.remove(index);
        list.add(newIndex.intValue(), n);
        //log.info("n = {}, index = {}, newIndex = {}", n, index, newIndex);

        return list;
    }
}


