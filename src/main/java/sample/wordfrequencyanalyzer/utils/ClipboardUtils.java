package sample.wordfrequencyanalyzer.utils;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClipboardUtils {
    private static final Logger logger = LoggerFactory.getLogger(ClipboardUtils.class);

    /**
     * Копирует текст в системный буфер обмена
     */
    public static void copyToClipboard(String text) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
        logger.info("Скопировано в буфер обмена: {} символов", text.length());
    }
}