package sample.wordfrequencyanalyzer.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sample.wordfrequencyanalyzer.controllers.MainController;

import java.util.*;

public class PhrasalParticle {

    public PhrasalParticle() {
    }


    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    private static List<String> prasalParticles = new ArrayList<>(Arrays.asList(
            "up", "down", "out", "in", "on", "off", "over", "through", "away",
            "back", "around", "about", "along", "across", "after", "ahead", "against",
            "aside", "behind", "by", "for", "forward", "into", "near", "past", "together",
            "under"));

    public static boolean isPhrasalParticle(String word) {
        if (word == null || word.trim().isEmpty()) return false;
        String cleanWord = word.trim().toLowerCase();
        // Отладка
        if (!prasalParticles.contains(cleanWord)) {
            logger.debug("❌ '" + word + "' -> '" + cleanWord + "' NOT FOUND");
            prasalParticles.forEach(p -> System.out.println("List: '" + p + "'"));
        }
        return prasalParticles.contains(cleanWord);
    }

}
