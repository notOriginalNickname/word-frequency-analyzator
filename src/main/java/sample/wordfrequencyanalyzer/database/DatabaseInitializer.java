package sample.wordfrequencyanalyzer.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sample.wordfrequencyanalyzer.controllers.MainController;

import java.sql.*;

public class DatabaseInitializer {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    public static void initialize() {
        createWordsTable();
        createPromptsTable();
        insertDefaultPrompts();
    }


    private static void createWordsTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS words (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                text TEXT NOT NULL UNIQUE,
                type TEXT NOT NULL CHECK(type IN ('SINGLE', 'PHRASAL')),
                mention_count INTEGER DEFAULT 0,
                is_recorded BOOLEAN DEFAULT 0,
                group_id INTEGER DEFAULT NULL
            )
            """;

        executeSQL(sql);
    }



    public static boolean isTablesExists() {

        if (tableExists("words")) {
            logger.info("Таблица 'words' успешно инициализирована");
        } else if (tableExists("prompts")) {
            logger.info("Таблица 'prompts' успешно инициализирована");
        }
        return true;
    }

    private static void createPromptsTable() {
        String sql = """
        CREATE TABLE IF NOT EXISTS prompts (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL UNIQUE,
            content TEXT NOT NULL
        )
        """;

        executeSQL(sql);
    }

    private static void executeSQL(String sql) {
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static PreparedStatement executeQuery(String sql, Object... params) throws SQLException {
        Connection connection = DatabaseManager.getInstance().getConnection();
        PreparedStatement pstmt = connection.prepareStatement(sql);

        for (int i = 0; i < params.length; i++) {
            pstmt.setObject(i + 1, params[i]);
        }

        return pstmt;
    }

    private static boolean tableExists(String tableName){
        String sql = String.format("SELECT name FROM sqlite_master WHERE type='{}' AND name=?", tableName);
        try (PreparedStatement pstmt = executeQuery(sql, tableName);
             ResultSet rs = pstmt.executeQuery()) {
            return rs.next();
        } catch (SQLException e) {
            logger.error("Ошибка проверки таблицы {}, причина: {}", tableName, e.getMessage());
            return false;
        }
    }


    // Дефолтный промпт
    private static void insertDefaultPrompts() {
        String checkSql = "SELECT COUNT(*) FROM prompts";

        try (Statement stmt = DatabaseManager.getInstance().getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {

            if (rs.next() && rs.getInt(1) == 0) {
                String insertSql = """
                INSERT INTO prompts (name, content) VALUES 
                ('Дефолтный промпт','Приведи, пожалуйста, для следующих слов по 5 предложений в контексте, чтобы я при изучении мог легче их запоминать. Если слово имеет несколько распространённых значений делай на каждое по 5 предложений и подписывай это. По ИМЕННО по такому паттерну: "предложение на русском с **выделенным изучаемым словом** -- a sentence in english with **the highlighted word under study**"
                  ** этим (двумя звёздочками) как раз выделено изучаемое слово, выделять нужно не всё предложение, а ТОЛЬКО изучаемое слово. Пример подобного предложения "Я играю в **футбол** -- I am playing **football**" football в данном предложении изучаемое слово. Первое предложение должно быть русским переводом с русским выделенным словом, а второе на английском и разделяться " -- ". Выделять изучаемое слово нужно подобным образом **слово**. Изучаемое слово должно быть на русском в русском предложении и на английском в английском. Если у слова несколько значений давай также строку, где я могу скопировать все значения слова через слеш (/) в таком виде " - **тест/тест/тест**"
                  Слова для изучения:')
                """;
                stmt.execute(insertSql);
                logger.debug("Дефолтный промпт успешно добавлен в БД");
            }
        } catch (SQLException e) {
            logger.error("Ошибка при вставке дефолтного промпта", e);
        }
    }
}