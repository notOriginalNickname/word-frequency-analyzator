package sample.wordfrequencyanalyzer.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import sample.wordfrequencyanalyzer.services.WordProcessorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sample.wordfrequencyanalyzer.utils.BackupWordsLogs;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static sample.wordfrequencyanalyzer.utils.Notifications.showAlert;

public class MainController implements Initializable {

    @FXML
    private TextArea inputArea;
    @FXML
    private Label resultLabel;

    private WordProcessorService wordProcessorService;
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        wordProcessorService = new WordProcessorService();

    }
    @FXML
    private void handleProcessWords() {
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
            wordProcessorService.processWords(lines);
            resultLabel.setText("Обработано слов: " + lines.size());
            inputArea.clear();
            logger.info("Слова успешно обработаны");
        } catch (Exception e) {
            logger.error("Ошибка при обработке: {}", e.getMessage());
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось обработать слова: " + e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        logger.debug("handleClear вызван");
        inputArea.clear();
        resultLabel.setText("Окно очищено");
    }


}