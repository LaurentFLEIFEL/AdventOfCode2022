package com.lfl.advent2022.days;

import com.lfl.advent2022.LinesConsumer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.function.TriFunction;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.factory.Lists;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class Day7 implements LinesConsumer {

    private static final Directory ROOT = Directory.of(null, "dir /");
    @Getter
    private long sum;
    @Getter
    private long size;

    @Override
    public void consume(List<String> lines) {
        buildTree(lines);

        sum = ROOT.listDirectories()
                .select(directory -> directory.size() < 100_000)
                .collectLong(Directory::size)
                .sum();

        log.info("sum = {}", sum);

        long totalSize = 70_000_000L;
        long neededSpace = 30_000_000 - (totalSize - ROOT.size());

        Directory min = ROOT.listDirectories()
                .select(directory -> directory.size() >= neededSpace)
                .min(Comparator.comparingLong(Directory::size));

        size = min.size();
        log.info("size = {}", size);
    }

    private static void buildTree(List<String> lines) {
        Directory current = null;
        for (int index = 0; index < lines.size(); ) {
            Command command = Command.of(lines.get(index));
            current = command.apply(current, lines, index);
            index = command.shiftIndex(current, lines, index);
        }
    }

    private static abstract class Entry {
        @Getter
        protected Directory parent;
        @Getter
        protected String name;

        public abstract long size();

        public abstract boolean isFile();

        public static Entry of(Directory parent, String line) {
            if (line.startsWith("dir")) {
                return Directory.of(parent, line);
            }
            return File.of(parent, line);
        }
    }

    private static class Directory extends Entry {
        @Getter
        private final MutableList<Entry> children = Lists.mutable.empty();

        public Directory(Directory parent, String name) {
            this.parent = parent;
            this.name = name;
        }

        public static Directory of(Directory parent, String line) {
            return new Directory(parent, line.substring(4));
        }

        public void addChild(Entry child) {
            children.add(child);
        }

        @Override
        public long size() {
            return children.collectLong(Entry::size).sum();
        }

        @Override
        public boolean isFile() {
            return false;
        }

        public MutableList<Directory> listDirectories() {
            return children.select(Predicates.not(Entry::isFile))
                    .collect(Directory.class::cast)
                    .flatCollect(Directory::listDirectories)
                    .with(this);
        }
    }

    private static class File extends Entry {
        @Getter
        private final long size;

        public File(Directory parent, String name, long size) {
            this.parent = parent;
            this.name = name;
            this.size = size;
        }

        public static File of(Directory parent, String line) {
            return new File(parent, line.split(" ")[1], Integer.parseInt(line.split(" ")[0]));
        }

        @Override
        public long size() {
            return size;
        }

        @Override
        public boolean isFile() {
            return true;
        }
    }

    private enum Command {
        CD((current, output, index) -> {
            String argument = output.get(index).substring(5);
            if ("..".equals(argument)) {
                return current.getParent();
            }
            if ("/".equals(argument)) {
                return ROOT;
            }
            return (Directory) current.getChildren().detect(entry -> entry.name.equals(argument));
        },
                (current, output, index) -> index + 1,
                "$ cd "),
        LIST((current, output, index) -> {
            index++;
            while (index < output.size() && !output.get(index).startsWith("$")) {
                current.addChild(Entry.of(current, output.get(index)));
                index++;
            }
            return current;
        },
                (current, output, index) -> current.getChildren().size() + 1 + index,
                "$ ls");

        @Getter
        private final TriFunction<Directory, List<String>, Integer, Directory> result;
        @Getter
        private final TriFunction<Directory, List<String>, Integer, Integer> indexShifter;

        private final String prefix;

        Command(TriFunction<Directory, List<String>, Integer, Directory> result,
                TriFunction<Directory, List<String>, Integer, Integer> indexShifter,
                String prefix) {
            this.result = result;
            this.indexShifter = indexShifter;
            this.prefix = prefix;
        }

        public static Command of(String line) {
            return Arrays.stream(Command.values())
                    .filter(command -> line.startsWith(command.prefix))
                    .findAny()
                    .orElseThrow(IllegalArgumentException::new);
        }

        public Directory apply(Directory current, List<String> output, int index) {
            return this.result.apply(current, output, index);
        }

        public int shiftIndex(Directory current, List<String> output, int index) {
            return this.indexShifter.apply(current, output, index);
        }
    }

}
