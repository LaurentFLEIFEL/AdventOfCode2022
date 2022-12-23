package com.lfl.advent2022;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public interface LinesConsumer {

    void consume(MutableList<String> lines);

    @SuppressWarnings("ConstantConditions")
    static MutableList<String> readAllInput(String input) throws URISyntaxException, IOException {
        URI resource = LinesConsumer.class.getClassLoader().getResource("input/" + input).toURI();
        return Lists.mutable.ofAll(Files.readAllLines(Paths.get(resource)));
    }
}
