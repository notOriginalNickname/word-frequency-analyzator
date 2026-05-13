package sample.wordfrequencyanalyzer.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sample.wordfrequencyanalyzer.dao.PromptDAO;
import sample.wordfrequencyanalyzer.models.Prompt;

import java.util.List;
import java.util.Optional;

public class PromptService {
    private static final Logger logger = LoggerFactory.getLogger(PromptService.class);
    private final PromptDAO promptDAO;

    public PromptService() {
        this.promptDAO = new PromptDAO();
    }

    public List<Prompt> getAllPrompts() {
        return promptDAO.findAll();
    }

    public Optional<Prompt> getPromptById(Long id) {
        return promptDAO.findById(id);
    }

    public Optional<Prompt> getPromptByName(String name) {
        return promptDAO.findByName(name);
    }

    public Prompt savePrompt(Prompt prompt) {
        return promptDAO.save(prompt);
    }

    public void deletePrompt(Long id) {
        promptDAO.delete(id);
    }

    public Optional<Prompt> getLastUsedPrompt() {
        return promptDAO.getLastUsed();
    }
}