package sample.wordfrequencyanalyzer.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sample.wordfrequencyanalyzer.controllers.MainController;
import sample.wordfrequencyanalyzer.database.DatabaseManager;
import sample.wordfrequencyanalyzer.models.Word;

import java.sql.*;
import java.util.*;

public class WordDAO {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    private Connection getConnection() throws SQLException {
        return DatabaseManager.getInstance().getConnection();
    }

    public Optional<Word> findByText(String text) {
        String sql = "SELECT * FROM words WHERE text = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, text);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToWord(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }


    public Word save(Word word) {
        if (word.getId() == null) {
            return insert(word);
        } else {
            return update(word);
        }
    }

    private Word insert(Word word) {
        String sql = "INSERT INTO words (text, type, mention_count, is_recorded, group_id) VALUES (?, ?, ?, ?, ?)";
        //                                                   добавить это ↑

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, word.getText());
            pstmt.setString(2, word.getType().name());
            pstmt.setInt(3, word.getMentionCount());
            pstmt.setBoolean(4, word.isRecorded());

            // Сохраняем group (может быть null)
            if (word.getGroupId() != null) {
                pstmt.setLong(5, word.getGroupId());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }

            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                word.setId(generatedKeys.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return word;
    }

    private Word update(Word word) {
        String sql = "UPDATE words SET text = ?, type = ?, mention_count = ?, is_recorded = ?, group_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, word.getText());
            pstmt.setString(2, word.getType().name());
            pstmt.setInt(3, word.getMentionCount());
            pstmt.setBoolean(4, word.isRecorded());

            if (word.getGroupId() != null) {
                pstmt.setLong(5, word.getGroupId());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }

            pstmt.setLong(6, word.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return word;
    }

    public List<Word> findAllOrderedByMentionCount() {
        String sql = "SELECT * FROM words ORDER BY mention_count DESC";
        List<Word> words = new ArrayList<>();

        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                words.add(mapResultSetToWord(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return words;
    }


    private Word mapResultSetToWord(ResultSet rs) throws SQLException {
        Word word = new Word();
        word.setId(rs.getLong("id"));
        word.setText(rs.getString("text"));
        word.setType(Word.WordType.valueOf(rs.getString("type")));
        word.setMentionCount(rs.getInt("mention_count"));
        word.setRecorded(rs.getBoolean("is_recorded"));

        // Читаем group_id (может быть NULL)
        long groupId = rs.getLong("group_id");
        if (!rs.wasNull()) {
            word.setGroupId(groupId);
        } else {
            word.setGroupId(null);
        }

        return word;
    }

    /**
     * Найти все слова по ID группы
     */
    public List<Word> findByGroup(Long groupId) {
        String sql;
        PreparedStatement pstmt = null;
        List<Word> words = new ArrayList<>();

        try {
            if (groupId == null) {
                // Ищем слова без группы (WHERE group IS NULL)
                sql = "SELECT * FROM words WHERE group_id IS NULL ORDER BY mention_count DESC";
                pstmt = getConnection().prepareStatement(sql);
            } else {
                // Ищем слова с конкретной группой
                sql = "SELECT * FROM words WHERE group_id = ? ORDER BY mention_count DESC";
                pstmt = getConnection().prepareStatement(sql);
                pstmt.setLong(1, groupId);
            }

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                words.add(mapResultSetToWord(rs));
            }
        } catch (SQLException e) {
            logger.error("Ошибка при поиске слов по группе: " + groupId, e);
        }

        return words;
    }


}
