package sample.wordfrequencyanalyzer.utils;

import java.io.File;

public class PathUtil {

 
    // Получаем путь к директории с jar/exe
    private static String getApplicationDirectory() {
        try {
            File jarFile = new File(PathUtil.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI());
            return jarFile.getParentFile().getAbsolutePath();
        } catch (Exception e) {
            throw new RuntimeException("Не удалось определить путь к приложению", e);
        }
    }

    // Получаем полный путь к папке WordAnalyzer
    public static String getDbDirectory() {
        return getApplicationDirectory() + File.separator + "WordAnalyzer";
    }

    // Получаем полный путь к файлу БД
    public static String getDbPath() {
        return getDbDirectory() + File.separator + "vocab.db";
    }

    public static String getBackupWordsPath() { return getDbDirectory() + File.separator + "backup_words.txt";}
 

}
