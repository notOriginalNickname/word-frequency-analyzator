package sample.wordfrequencyanalyzer.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import sample.wordfrequencyanalyzer.utils.PathUtil;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;

    // Путь к файлу БД в папке пользователя
    private static final String DATABASE_URL = "jdbc:sqlite:" + PathUtil.getDbPath();

    private DatabaseManager() {
        try {
            // Создаем папку, если её нет
            java.nio.file.Files.createDirectories(
                    java.nio.file.Paths.get(PathUtil.getDbDirectory())
            );

            // Подключаемся (файл создастся автоматически)
            connection = DriverManager.getConnection(DATABASE_URL);

            // Включаем поддержку внешних ключей
            connection.createStatement().execute("PRAGMA foreign_keys = ON");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DATABASE_URL);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

 
}
