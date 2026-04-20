package sample.wordfrequencyanalyzer.services;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sample.wordfrequencyanalyzer.controllers.MainController;
import sample.wordfrequencyanalyzer.utils.BackupWordsLogs;
import sample.wordfrequencyanalyzer.utils.WordService;

import java.util.Arrays;
import java.util.List;

import static sample.wordfrequencyanalyzer.utils.Notifications.showAlert;

public class MainService {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    private final WordService wordService = new WordService();


    public void processWordsFromPage (TextArea inputArea, Label resultLabel) {
        logger.debug("handleProcessWords вызван");

        String text = inputArea.getText();
        if (text == null || text.trim().isEmpty()) {
            showAlert("Ошибка", "Введите слова для обработки");
            return;
        }

        BackupWordsLogs.appendToFile(System.getProperty("user.home") + "/WordAnalyzer/backup_words.txt",
                text, "UTF-8");
        List<String> lines = Arrays.asList(text.split("\\n"));
        logger.debug("Получено строк для обработки: {}", lines.size());

        try {
            wordService.processWords(lines);
            resultLabel.setText("Обработано слов: " + lines.size());
            inputArea.clear();
            logger.info("Слова успешно обработаны");
        } catch (Exception e) {
            logger.error("Ошибка при обработке: {}", e.getMessage());
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось обработать слова: " + e.getMessage());
        }
    }
}
