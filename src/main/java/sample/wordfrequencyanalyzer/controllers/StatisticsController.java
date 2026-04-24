package sample.wordfrequencyanalyzer.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sample.wordfrequencyanalyzer.dao.WordDAO;
import sample.wordfrequencyanalyzer.models.Word;
import sample.wordfrequencyanalyzer.models.WordGroupDisplay;
import sample.wordfrequencyanalyzer.services.StatisticsService;
import sample.wordfrequencyanalyzer.utils.ClipboardUtils;
import sample.wordfrequencyanalyzer.utils.Router;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static sample.wordfrequencyanalyzer.utils.Notifications.showAlert;
import static sample.wordfrequencyanalyzer.utils.Notifications.showInfo;

public class StatisticsController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(StatisticsController.class);

    @FXML
    private TableView<WordGroupDisplay> wordsTable;

    @FXML
    private TableColumn<WordGroupDisplay, String> wordsColumn;

    @FXML
    private TableColumn<WordGroupDisplay, Integer> mentionsColumn;

    @FXML
    private Button copyButton;

    @FXML
    private Button copyWithPromptButton;

    @FXML
    private PromptSelectorController promptSelectorController;


    private StatisticsService statisticsService;
    private ObservableList<WordGroupDisplay> tableData;
    private WordDAO wordDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("Инициализация StatisticsController");

        ControllerManager.getInstance().setStatisticsController(this);

        statisticsService = new StatisticsService();
        wordDAO = new WordDAO();
        tableData = FXCollections.observableArrayList();

        wordsColumn.setCellValueFactory(new PropertyValueFactory<>("words"));
        mentionsColumn.setCellValueFactory(new PropertyValueFactory<>("totalMentions"));

        wordsColumn.prefWidthProperty().bind(wordsTable.widthProperty().multiply(0.7));
        mentionsColumn.prefWidthProperty().bind(wordsTable.widthProperty().multiply(0.3));

        // Настройка множественного выбора
        wordsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Обработчик Ctrl+C
        setupKeyboardShortcuts();

        loadData();
    }

    private void setupKeyboardShortcuts() {
        // Ctrl+C для копирования
        KeyCombination ctrlC = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);

        wordsTable.setOnKeyPressed(event -> {
            if (ctrlC.match(event)) {
                copySelectedRows();
                event.consume();
            }
        });
    }

    @FXML
    private void copySelectedRows() {
        List<WordGroupDisplay> selectedItems = wordsTable.getSelectionModel().getSelectedItems();

        if (selectedItems.isEmpty()) {
            logger.info("Нет выделенных строк для копирования");
            showAlert("Информация", "Нет выделенных строк");
            return;
        }

        // Формируем текст для копирования
        String textToCopy = selectedItems.stream()
                .map(this::formatForCopy)
                .collect(Collectors.joining("\n"));

        // Обновляем статистику в БД
        updateStatisticsForSelected(selectedItems);

        ClipboardUtils.copyToClipboard(textToCopy);

        int quantityOfCopiedItems = selectedItems.size();

        // Обновляем таблицу
        loadData();

        // Показываем сообщение о количестве скопированных элементов
        showInfo("Скопировано",
                String.format("Скопировано %d элементов", quantityOfCopiedItems));

        logger.info("Скопировано {} строк", quantityOfCopiedItems);

    }

    /**
     * Обновляет статистику для выделенных элементов
     */
    private void updateStatisticsForSelected(List<WordGroupDisplay> selectedItems) {
        for (WordGroupDisplay item : selectedItems) {
            if (item.isGroup()) {
                // Обновляем все слова в группе
                updateWordsInGroup(item);
            } else {
                // Обновляем одно слово
                updateSingleWord(item);
            }
        }
    }

    /**
     * Обновляет одиночное слово из строки вида "word (5)" /  "look for (1)"
     */
    private void updateSingleWord(WordGroupDisplay item) {

        String wordText = extractWordText(item.getWords());

        Optional<Word> wordOpt = wordDAO.findByText(wordText);
        if (wordOpt.isPresent()) {
            Word word = wordOpt.get();
            word.setMentionCount(0);
            word.setRecorded(true);
            wordDAO.save(word);
            logger.debug("Обновлено слово: {}, recorded=true, mentionCount=0", wordText);
        }
    }

    /**
     * Обновляет все слова в группе - "word (3), hello (1)"
     */
    private void updateWordsInGroup(WordGroupDisplay groupItem) {

        String wordsPart = groupItem.getWords();
        String[] wordsWithNumbers = wordsPart.split(", ");

        for (String wordWithNumber : wordsWithNumbers) {
            String wordText = extractWordText(wordWithNumber);

            Optional<Word> wordOpt = wordDAO.findByText(wordText);
            if (wordOpt.isPresent()) {
                Word word = wordOpt.get();
                word.setMentionCount(0);
                word.setRecorded(true);
                wordDAO.save(word);
                logger.debug("Обновлено слово в группе: {}, recorded=true, mentionCount=0", wordText);
            }
        }
    }

    /**
     * Извлекает текст слова из строки вида "word (5)"
     */
    private String extractWordText(String wordWithNumber) {
        return wordWithNumber.replaceAll("\\s*\\(\\d+\\)", "").trim();
    }

    /**
     * Форматирует строку для копирования
     * Если слово одно "word (5)" - просто слово
     * Если группа "word1 (2), word2 (5), word3 (1)" - слова через " or "
     */
    private String formatForCopy(WordGroupDisplay item) {
        if (item.isGroup()) {
            String wordsPart = item.getWords();

            // Извлекаем только слова без чисел
            String[] wordsWithNumbers = wordsPart.split(", ");
            List<String> words = java.util.Arrays.stream(wordsWithNumbers)
                    .map(w -> extractWordText(w))
                    .collect(Collectors.toList());

            return String.join(" or ", words);
        } else {
            // Одиночное слово - извлекаем слово без числа
            String wordPart = item.getWords();
            return extractWordText(wordPart);
        }
    }

    private void loadData() {
        tableData.clear();
        tableData.addAll(statisticsService.getAllWordsGrouped());
        wordsTable.setItems(tableData);
        logger.info("Загружено {} строк", tableData.size());
    }


    public void refreshData() {
        logger.info("Автоматическое обновление данных");
        promptSelectorController.refreshPrompts();
        loadData();
    }

    @FXML
    private void copyWithPrompt() {
        List<WordGroupDisplay> selectedItems = wordsTable.getSelectionModel().getSelectedItems();

        if (selectedItems.isEmpty()) {
            showAlert("Информация", "Нет выделенных строк");
            return;
        }

        int quantityOfCopiedItems = selectedItems.size();

        // Формируем текст
        String wordsText = selectedItems.stream()
                .map(this::formatForCopy)
                .collect(Collectors.joining("\n"));

        // Получаем промпт
        String promptText = promptSelectorController.getSelectedPromptText();

        // Формируем финальный текст для копирования из промпта и слов
        String finalText;
        if (promptText.isEmpty()) {
            finalText = wordsText;
        } else {
            finalText = promptText + "\n" + wordsText;
        }

        // Обновляем статистику
        updateStatisticsForSelected(selectedItems);

        // Копируем в буфер обмена
        ClipboardUtils.copyToClipboard(finalText);

        // Перезагружаем таблицу
        loadData();

        showInfo("Скопировано",
                String.format("Скопировано %d элементов с промптом", quantityOfCopiedItems));
    }

}