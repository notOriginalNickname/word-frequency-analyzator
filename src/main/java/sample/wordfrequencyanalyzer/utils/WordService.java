package sample.wordfrequencyanalyzer.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sample.wordfrequencyanalyzer.controllers.MainController;
import sample.wordfrequencyanalyzer.dao.WordDAO;
import sample.wordfrequencyanalyzer.models.Word;

import java.util.*;
import java.util.stream.Collectors;


public class WordService {

    private final WordDAO wordDAO;

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    private final PhrasalDetector phrasalDetector = new PhrasalDetector();

    public WordService() {
        this.wordDAO = new WordDAO();
    }

    /**
     * Основной метод обработки списка строк
     */
    public void processWords(List<String> lines) {
        for (String line : lines) {
            String trimmed = line.trim().toLowerCase();

            if (trimmed.isEmpty()) continue;

            // Определяем тип строки и обрабатываем соответственно
            if (trimmed.contains(" or ")) {
                processSimilarWords(trimmed);
            } else if (phrasalDetector.isPhrasal(trimmed)) {
                // Фразовый глагол (есть пробелы, но нет "or")
                processPhrasalVerb(trimmed);
            } else {
                processSingleWord(trimmed);
            }
        }
    }

    /**
     * Обработка одиночного слова
     */
    private void processSingleWord(String wordText) {
        Optional<Word> existingWord = wordDAO.findByText(wordText);

        if (existingWord.isPresent()) {
            // Слово уже существует - увеличиваем счетчик
            Word word = existingWord.get();
            word.setMentionCount(word.getMentionCount() + 1);
            wordDAO.save(word);
            logger.debug("Слово '" + wordText + "' обновлено. Теперь упоминаний: " + word.getMentionCount());
        } else {
            // Новое слово - создаем
            Word newWord = new Word(wordText, Word.WordType.SINGLE);
            wordDAO.save(newWord);
            logger.debug("Добавлено новое слово: '" + wordText + "'");
        }
    }

    private void processSingleWordWithGroup(String wordText, Long group) {
        Optional<Word> existingWord = wordDAO.findByText(wordText);

        if (existingWord.isPresent()) {
            // Слово уже существует - увеличиваем счетчик
            Word word = existingWord.get();
            word.setMentionCount(word.getMentionCount() + 1);
            word.setGroupId(group);
            wordDAO.save(word);
            logger.debug("Слово '" + wordText + "' обновлено и добавлено в группу " + group + ". Теперь упоминаний: " + word.getMentionCount());
        } else {
            // Новое слово - создаем
            Word newWord = new Word(wordText, Word.WordType.SINGLE);
            newWord.setGroupId(group);
            wordDAO.save(newWord);
            logger.debug("Добавлено новое слово: '" + wordText + "'" + " в группу " + group);
        }
    }

    /**
     * Обработка фразового глагола
     */
    private void processPhrasalVerb(String phraseText) {

        Optional<Word> existingPhrase = wordDAO.findByText(phraseText);

        if (existingPhrase.isPresent()) {
            // Фразовый глагол уже существует - увеличиваем счетчик
            Word phrase = existingPhrase.get();
            phrase.setMentionCount(phrase.getMentionCount() + 1);
            wordDAO.save(phrase);
            logger.debug("Фразовый глагол '" + phraseText + "' обновлен. Теперь упоминаний: " + phrase.getMentionCount());
        } else {
            // Новый фразовый глагол - создаем
            Word newPhrase = new Word(phraseText, Word.WordType.PHRASAL);
            wordDAO.save(newPhrase);
            logger.debug("Добавлен новый фразовый глагол: '" + phraseText + "'");
        }
    }

    /**
     * Обработка схожих слов (содержат "or")
     * Например: "fix or repair" или "happy or joyful or glad"
     */

    private void processSimilarWords(String phraseText) {
        // Разбиваем фразу на отдельные слова
        String[] words = phraseText.split("\\s+or\\s+");
        List<String> wordList = Arrays.stream(words)
                .map(String::trim)
                .filter(w -> !w.isEmpty())
                .collect(Collectors.toList());

        if (wordList.isEmpty()) return;

        logger.debug("Обработка группы схожих слов: {}", phraseText);

        // Шаг 1: Собираем все существующие слова и их группы
        Set<Long> existingGroups = new HashSet<>();
        Map<String, Word> existingWordsMap = new HashMap<>();
        List<String> newWordsList = new ArrayList<>();
        boolean hasExistingWords = false;  // ← Новый флаг

        for (String wordText : wordList) {
            Optional<Word> existingWordOpt = wordDAO.findByText(wordText);

            if (existingWordOpt.isPresent()) {
                Word existingWord = existingWordOpt.get();
                existingWordsMap.put(wordText, existingWord);
                hasExistingWords = true;  // ← Есть существующее слово

                if (existingWord.getGroupId() != null) {
                    existingGroups.add(existingWord.getGroupId());
                    logger.debug("Слово '{}' уже в группе: {}", wordText, existingWord.getGroupId());
                } else {
                    logger.debug("Слово '{}' существует, но без группы", wordText);
                }
            } else {
                newWordsList.add(wordText);
                logger.debug("Слово '{}' будет создано", wordText);
            }
        }

        // Шаг 2: Определяем ID группы
        Long targetGroupId;

        if (!existingGroups.isEmpty()) {
            // Случай 1: Есть слова с группами - используем первую
            targetGroupId = existingGroups.iterator().next();

            if (existingGroups.size() > 1) {
                // Объединяем разные группы
                logger.debug("Слова в разных группах: {}. Объединяем в {}", existingGroups, targetGroupId);
                for (Long otherGroupId : existingGroups) {
                    if (!otherGroupId.equals(targetGroupId)) {
                        mergeGroups(otherGroupId, targetGroupId);
                    }
                }
            } else {
                logger.debug("Все слова с группами в одной группе: {}", targetGroupId);
            }
        } else if (hasExistingWords) {
            // Случай 2: Есть существующие слова, но все без группы
            // Создаём новую группу для них
            targetGroupId = generateNewGroupId();
            logger.debug("Есть существующие слова без групп, создаём новую группу: {}", targetGroupId);
        } else {
            // Случай 3: Все слова новые
            targetGroupId = generateNewGroupId();
            logger.debug("Все слова новые, создаём группу: {}", targetGroupId);
        }

        // Шаг 3: Обновляем/создаём все слова
        for (Word existingWord : existingWordsMap.values()) {
            // Увеличиваем счётчик
            existingWord.setMentionCount(existingWord.getMentionCount() + 1);

            // Если слово было без группы - теперь оно получает группу
            if (existingWord.getGroupId() == null) {
                existingWord.setGroupId(targetGroupId);
                logger.debug("Слово '{}' получает группу {}", existingWord.getText(), targetGroupId);
            } else if (!targetGroupId.equals(existingWord.getGroupId())) {
                // Если было в другой группе - переносим
                existingWord.setGroupId(targetGroupId);
                logger.debug("Слово '{}' переносится в группу {}", existingWord.getText(), targetGroupId);
            }

            wordDAO.save(existingWord);
        }

        // Создаём новые слова
        for (String newWordText : newWordsList) {
            if (phrasalDetector.isPhrasal(newWordText)) {
                processPhrasalVerb(newWordText);
            } else {
                Word newWord = new Word(newWordText, Word.WordType.SINGLE, targetGroupId);
                wordDAO.save(newWord);
                logger.debug("Создано новое слово '{}' в группе {}", newWordText, targetGroupId);
            }
        }

        logger.debug("Готово. Группа {} содержит {} слов", targetGroupId, wordList.size());
    }


    /**
     * Объединяет две группы: все слова из sourceGroupId переносятся в targetGroupId
     */
    private void mergeGroups(Long sourceGroupId, Long targetGroupId) {
        if (sourceGroupId.equals(targetGroupId)) return;

        logger.debug("Объединение группы {} в группу {}", sourceGroupId, targetGroupId);

        // Переносим все слова из исходной группы в целевую
        List<Word> wordsInSourceGroup = wordDAO.findByGroup(sourceGroupId);

        for (Word word : wordsInSourceGroup) {
            word.setGroupId(targetGroupId);
            wordDAO.save(word);
            logger.debug("Слово '{}' перенесено из группы {} в {}",
                    word.getText(), sourceGroupId, targetGroupId);
        }


    }


    public static Long generateNewGroupId() {
        return System.currentTimeMillis();
    }
}