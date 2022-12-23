package com.lfl.advent2022.days;

import com.lfl.advent2022.LinesConsumer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.list.MutableList;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Day6 implements LinesConsumer {

    @Getter
    private int startOfPacket;
    @Getter
    private int startOfMessage;

    @Override
    public void consume(MutableList<String> lines) {
        String line = lines.get(0);

        startOfPacket = startOfPacket(line);
        startOfMessage = startOfMessage(line);

        log.info("startOfPacket = {}", startOfPacket);
        log.info("startOfMessage = {}", startOfMessage);
    }

    private static int startOfPacket(String line) {
        return indexOfNDifferentCharacter(line, 4);
    }

    private static int startOfMessage(String line) {
        return indexOfNDifferentCharacter(line, 14);
    }

    private static int indexOfNDifferentCharacter(String line, int finalNbrOfDifferentCharacter) {
        int nbrOfDifferentCharacter = 0;
        int finalIndex = 0;
        for (int index = 0; index < line.length(); index++) {
            if (nbrOfDifferentCharacter == finalNbrOfDifferentCharacter) {
                finalIndex = index;
                break;
            }

            char c = line.charAt(index);
            int indexOf = line.indexOf(c, index + 1);
            if (indexOf == -1 || (indexOf - index) >= (finalNbrOfDifferentCharacter - nbrOfDifferentCharacter)) {
                nbrOfDifferentCharacter++;
            } else {
                nbrOfDifferentCharacter = 0;
            }
        }
        return finalIndex;
    }
}
