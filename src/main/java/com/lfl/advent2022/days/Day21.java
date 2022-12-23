package com.lfl.advent2022.days;

import com.lfl.advent2022.LinesConsumer;
import com.lfl.advent2022.utils.StringHelper;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.collector.Collectors2;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class Day21 implements LinesConsumer {
    private static Map<String, Expression> expressions;

    @Override
    public void consume(MutableList<String> lines) {
        expressions = lines.stream()
                .map(Expression::of)
                .collect(Collectors2.toMap(
                        Expression::getId,
                        expr -> expr
                ));
        log.info("expressions = {}", expressions.size());

        BigInteger result = expressions.get("root").evaluate();
        log.info("result = {}", result);

        BigInteger result2 = evaluateHumn();
        log.info("result = {}", result2);
    }

    private static BigInteger evaluateHumn() {
        Add root = (Add) expressions.get("root");
        Expression expr1 = expressions.get(root.getExpr1());
        Expression expr2 = expressions.get(root.getExpr2());

        if (expr1.containsHumn()) {
            log.info("eval2 = {}", expr2.evaluate());
            return expr1.evaluateHumn(expr2.evaluate());
        } else {
            log.info("eval1 = {}", expr1.evaluate());
            return expr2.evaluateHumn(expr1.evaluate());
        }
    }

    private sealed interface Expression {
        Pattern pattern = Pattern.compile("(?<expr1>\\w+) (?<op>[+\\-/*]) (?<expr2>\\w+)");

        String getId();

        boolean containsHumn();

        BigInteger evaluateHumn(BigInteger target);

        BigInteger evaluate();

        private static Expression of(String line) {
            String[] split = line.split(": ");
            String id = split[0];
            if (StringHelper.isNumeric(split[1])) {
                int value = Integer.parseInt(split[1]);
                return Number.of(id, value);
            }

            Matcher matcher = pattern.matcher(split[1]);
            matcher.find();

            String expr1 = matcher.group("expr1");
            String op = matcher.group("op");
            String expr2 = matcher.group("expr2");

            return switch (op) {
                case "+" -> Day21.Add.of(id, expr1, expr2);
                case "-" -> Minus.of(id, expr1, expr2);
                case "*" -> Multiply.of(id, expr1, expr2);
                case "/" -> Divide.of(id, expr1, expr2);
                default -> null;
            };
        }
    }

    @Data
    @Builder
    private static final class Number implements Expression {
        private String id;
        private BigInteger value;

        private static Number of(String id, int value) {
            return Number.builder().id(id).value(BigInteger.valueOf(value)).build();
        }

        @Override
        public boolean containsHumn() {
            return "humn".equals(id);
        }

        @Override
        public BigInteger evaluateHumn(BigInteger target) {
            return target;
        }

        @Override
        public BigInteger evaluate() {
            return value;
        }
    }

    @Data
    @Builder
    private static final class Add implements Expression {
        private String id;
        private String expr1;
        private String expr2;

        private static Add of(String id, String expr1, String expr2) {
            return Add.builder().id(id).expr1(expr1).expr2(expr2).build();
        }

        @Override
        public boolean containsHumn() {
            return expressions.get(expr1).containsHumn() || expressions.get(expr2).containsHumn();
        }

        @Override
        public BigInteger evaluateHumn(BigInteger target) {
            Expression expression1 = expressions.get(expr1);
            Expression expression2 = expressions.get(expr2);
            if (expression1.containsHumn()) {
                return expression1.evaluateHumn(target.add(expression2.evaluate().negate()));
            } else {
                return expression2.evaluateHumn(target.add(expression1.evaluate().negate()));
            }
        }

        @Override
        public BigInteger evaluate() {
            return expressions.get(expr1).evaluate().add(expressions.get(expr2).evaluate());
        }
    }

    @Data
    @Builder
    private static final class Minus implements Expression {
        private String id;

        private String expr1;
        private String expr2;

        private static Minus of(String id, String expr1, String expr2) {
            return Minus.builder().id(id).expr1(expr1).expr2(expr2).build();
        }

        @Override
        public boolean containsHumn() {
            return expressions.get(expr1).containsHumn() || expressions.get(expr2).containsHumn();
        }

        @Override
        public BigInteger evaluateHumn(BigInteger target) {
            Expression expression1 = expressions.get(expr1);
            Expression expression2 = expressions.get(expr2);
            if (expression1.containsHumn()) {
                return expression1.evaluateHumn(target.add(expression2.evaluate()));
            } else {
                return expression2.evaluateHumn(expression1.evaluate().add(target.negate()));
            }
        }

        @Override
        public BigInteger evaluate() {
            return expressions.get(expr1).evaluate().add(expressions.get(expr2).evaluate().negate());
        }
    }

    @Data
    @Builder
    private static final class Multiply implements Expression {
        private String id;
        private String expr1;
        private String expr2;

        private static Multiply of(String id, String expr1, String expr2) {
            return Multiply.builder().id(id).expr1(expr1).expr2(expr2).build();
        }

        @Override
        public boolean containsHumn() {
            return expressions.get(expr1).containsHumn() || expressions.get(expr2).containsHumn();
        }

        @Override
        public BigInteger evaluateHumn(BigInteger target) {
            Expression expression1 = expressions.get(expr1);
            Expression expression2 = expressions.get(expr2);
            if (expression1.containsHumn()) {
                return expression1.evaluateHumn(target.divide(expression2.evaluate()));
            } else {
                return expression2.evaluateHumn(target.divide(expression1.evaluate()));
            }
        }

        @Override
        public BigInteger evaluate() {
            return expressions.get(expr1).evaluate().multiply(expressions.get(expr2).evaluate());
        }
    }

    @Data
    @Builder
    private static final class Divide implements Expression {
        private String id;
        private String expr1;
        private String expr2;

        private static Divide of(String id, String expr1, String expr2) {
            return Divide.builder().id(id).expr1(expr1).expr2(expr2).build();
        }

        @Override
        public boolean containsHumn() {
            return expressions.get(expr1).containsHumn() || expressions.get(expr2).containsHumn();
        }

        @Override
        public BigInteger evaluateHumn(BigInteger target) {
            Expression expression1 = expressions.get(expr1);
            Expression expression2 = expressions.get(expr2);
            if (expression1.containsHumn()) {
                return expression1.evaluateHumn(target.multiply(expression2.evaluate()));
            } else {
                return expression2.evaluateHumn(expression1.evaluate().divide(target));
            }
        }

        @Override
        public BigInteger evaluate() {
            return expressions.get(expr1).evaluate().divide(expressions.get(expr2).evaluate());
        }
    }
}
