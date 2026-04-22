package sample.wordfrequencyanalyzer.services;

import org.junit.jupiter.api.Test;
import sample.wordfrequencyanalyzer.utils.PhrasalDetector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class PhrasalDetectorServiceTest {
    private final PhrasalDetector phrasalDetector = new PhrasalDetector();

    private List<String> phrasal = new ArrayList<>(Arrays.asList(
            "turn off", "give up", "look out", "run away", "look for", "put up with", "look forward to", "get away with",
            "catch up with", "run out of", "run away"
    ));

    private List<String> couple = new ArrayList<>(Arrays.asList(
            "a chair", "the table", "to look", "knit hat", "good luck", "long way", "high speed", "the bank", "a car", "work hard", "drive fast"
    ));

    private List<String> single = new ArrayList<>(Arrays.asList(
            "chair", "table", "look", "knit"
    ));

    private List<String> several = new ArrayList<>(Arrays.asList(
            "a wood chair", "the table on the floor", "to look for hat", "knit hat is on the shelf"
    ));

    private List<String> notPhrasal = Stream.of(single, couple, several)
            .flatMap(List::stream)
            .collect(Collectors.toList());


    @Test
    void testRealPhrasalVerbs() {
        for (int i = 0; i < phrasal.size(); i++) {
            assertTrue(phrasalDetector.isPhrasal(phrasal.get(i)),
                    " ❌ " + phrasal.get(i) + " должен быть фразовым глаголом!" + " ❌ \n"
            );
        }
    }

    @Test
    void testNotPhrasalVerbs() {
        for (int i = 0; i < notPhrasal.size(); i++) {
            assertFalse(phrasalDetector.isPhrasal(notPhrasal.get(i)),
                    " ❌ " + notPhrasal.get(i) + " НЕ фразовый глагол!" + " ❌ \n"
            );
        }
    }


}
