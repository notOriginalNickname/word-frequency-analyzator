package sample.wordfrequencyanalyzer.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sample.wordfrequencyanalyzer.controllers.MainController;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;

public class BackupWordsLogs {
    /**
     * На всякий случай, чтобы слова сохранялись, как они вводились
     * **/

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    public static void appendToFile(String filePath, String text, String charset) {
        try (FileWriter writer = new FileWriter(filePath, Charset.forName(charset), true)) {
            writer.write(text + "\n");
            logger.info("Слова записаны в запасной файл");
        } catch (IOException e) {
            logger.error("Ошибка записи слов в запасной файл: {}" + e.getMessage());
        }
    }
}
