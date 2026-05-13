package sample.wordfrequencyanalyzer.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sample.wordfrequencyanalyzer.database.DatabaseManager;
import sample.wordfrequencyanalyzer.models.Prompt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PromptDAO {
    private static final Logger logger = LoggerFactory.getLogger(PromptDAO.class);

    private Connection getConnection() throws SQLException {
        return DatabaseManager.getInstance().getConnection();
    }

    public List<Prompt> findAll() {
        String sql = "SELECT * FROM prompts ORDER BY name";
        List<Prompt> prompts = new ArrayList<>();

        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                prompts.add(mapResultSetToPrompt(rs));
            }
        } catch (SQLException e) {
            logger.error("Ошибка при получении всех промптов", e);
        }

        return prompts;
    }

    public Optional<Prompt> findById(Long id) {
        String sql = "SELECT * FROM prompts WHERE id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToPrompt(rs));
            }
        } catch (SQLException e) {
            logger.error("Ошибка при поиске промпта по id: " + id, e);
        }

        return Optional.empty();
    }

    public Optional<Prompt> findByName(String name) {
        String sql = "SELECT * FROM prompts WHERE name = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToPrompt(rs));
            }
        } catch (SQLException e) {
            logger.error("Ошибка при поиске промпта по имени: " + name, e);
        }

        return Optional.empty();
    }

    public Prompt save(Prompt prompt) {
        if (prompt.getId() == null) {
            return insert(prompt);
        } else {
            return update(prompt);
        }
    }

    private Prompt insert(Prompt prompt) {
        String sql = "INSERT INTO prompts (name, content) VALUES (?, ?)";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, prompt.getName());
            pstmt.setString(2, prompt.getContent());
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                prompt.setId(generatedKeys.getLong(1));
            }
        } catch (SQLException e) {
            logger.error("Ошибка при вставке промпта", e);
        }

        return prompt;
    }

    private Prompt update(Prompt prompt) {
        String sql = "UPDATE prompts SET name = ?, content = ? WHERE id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, prompt.getName());
            pstmt.setString(2, prompt.getContent());
            pstmt.setLong(3, prompt.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении промпта", e);
        }

        return prompt;
    }

    public void delete(Long id) {
        String sql = "DELETE FROM prompts WHERE id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Ошибка при удалении промпта: " + id, e);
        }
    }

    public Optional<Prompt> getLastUsed() {
        String sql = "SELECT * FROM prompts ORDER BY id DESC LIMIT 1";

        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return Optional.of(mapResultSetToPrompt(rs));
            }
        } catch (SQLException e) {
            logger.error("Ошибка при получении последнего использованного промпта", e);
        }

        return Optional.empty();
    }

    private Prompt mapResultSetToPrompt(ResultSet rs) throws SQLException {
        Prompt prompt = new Prompt();
        prompt.setId(rs.getLong("id"));
        prompt.setName(rs.getString("name"));
        prompt.setContent(rs.getString("content"));
        return prompt;
    }
}