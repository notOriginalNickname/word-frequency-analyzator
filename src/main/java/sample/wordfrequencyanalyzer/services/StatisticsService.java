package sample.wordfrequencyanalyzer.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sample.wordfrequencyanalyzer.dao.WordDAO;
import sample.wordfrequencyanalyzer.models.Word;
import sample.wordfrequencyanalyzer.models.WordGroupDisplay;

import java.util.*;
import java.util.stream.Collectors;

public class StatisticsService {
    private static final Logger logger = LoggerFactory.getLogger(StatisticsService.class);
    private final WordDAO wordDAO;

    public StatisticsService() {
        this.wordDAO = new WordDAO();
    }

    /**
     * Получает все слова для отображения в таблице
     * Группирует слова по группам
     */
    public List<WordGroupDisplay> getAllWordsGrouped() {
        List<Word> allWords = wordDAO.findAllOrderedByMentionCount();
        List<WordGroupDisplay> result = new ArrayList<>();

        // Группируем слова по groupId
        Map<Long, List<Word>> groupedWords = new HashMap<>();
        List<Word> singleWords = new ArrayList<>();

        for (Word word : allWords) {
            if (word.getGroupId() != null) {
                groupedWords.computeIfAbsent(word.getGroupId(), k -> new ArrayList<>()).add(word);
            } else {
                singleWords.add(word);
            }
        }

        // Добавляем группы
        for (Map.Entry<Long, List<Word>> entry : groupedWords.entrySet()) {
            List<Word> wordsInGroup = entry.getValue();

            // Сортируем слова в группе по убыванию упоминаний
            wordsInGroup.sort((w1, w2) ->
                    Integer.compare(w2.getMentionCount(), w1.getMentionCount()));

            // Формируем строку со словами через запятую
            String wordsStr = wordsInGroup.stream()
                    .map(w -> w.getText() + " (" + w.getMentionCount() + ")")
                    .collect(Collectors.joining(", "));

            // Общее количество упоминаний в группе
            int totalMentions = wordsInGroup.stream()
                    .mapToInt(Word::getMentionCount)
                    .sum();

            result.add(new WordGroupDisplay(wordsStr, totalMentions, true, entry.getKey()));
        }

        // Добавляем одиночные слова (каждое в отдельной строке)
        for (Word word : singleWords) {
            result.add(new WordGroupDisplay(
                    word.getText() + " (" + word.getMentionCount() + ")",
                    word.getMentionCount(),
                    false,
                    null
            ));
        }

        // Сортируем результат по убыванию упоминаний
        result.sort((d1, d2) ->
                Integer.compare(d2.getTotalMentions(), d1.getTotalMentions()));

        return result;
    }
}