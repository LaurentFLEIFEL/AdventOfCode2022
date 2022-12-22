package com.lfl.advent2022.utils;

import org.eclipse.collections.api.factory.primitive.IntLists;
import org.eclipse.collections.api.list.primitive.ImmutableIntList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.set.primitive.ImmutableIntSet;
import org.eclipse.collections.impl.factory.primitive.IntSets;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class StringHelper {
    private static final Pattern INTS_PATTERN = Pattern.compile("(\\d+)");
    private static final ImmutableIntSet NUMERIC_CHARS = IntSets.immutable.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
    private static final ImmutableIntSet HEXADECIMAL_CHARS = IntSets.immutable.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f');

    public static boolean isNumeric(String input) {
        if (isEmpty(input)) {
            return false;
        }
        return input.chars().allMatch(NUMERIC_CHARS::contains);
    }

    public static boolean isHexadecimalNumeric(String input) {
        if (isEmpty(input)) {
            return false;
        }
        return input.chars().allMatch(HEXADECIMAL_CHARS::contains);
    }

    public static ImmutableIntList ints(String input) {
        Matcher matcher = INTS_PATTERN.matcher(input);

        return matcher.results()
                .map(MatchResult::group)
                .mapToInt(Integer::parseInt)
                .collect(IntLists.mutable::empty,
                        MutableIntList::add,
                        MutableIntList::withAll)
                .toImmutable();
    }
}
