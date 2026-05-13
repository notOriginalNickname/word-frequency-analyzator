package sample.wordfrequencyanalyzer.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sample.wordfrequencyanalyzer.models.Prompt;
import sample.wordfrequencyanalyzer.services.PromptService;
import sample.wordfrequencyanalyzer.utils.Router;

import java.net.URL;
import java.util.ResourceBundle;

public class PromptSelectorController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(PromptSelectorController.class);

    @FXML
    private ComboBox<Prompt> promptComboBox;

    private PromptService promptService;
    private ObservableList<Prompt> prompts;
    private Prompt lastSelectedPrompt;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        promptService = new PromptService();
        prompts = FXCollections.observableArrayList();

        promptComboBox.setCellFactory(lv -> new ListCell<Prompt>() {
            @Override
            protected void updateItem(Prompt item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });

        promptComboBox.setButtonCell(new ListCell<Prompt>() {
            @Override
            protected void updateItem(Prompt item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });

        loadPrompts();

        // Сохраняем последний выбранный промпт
        promptComboBox.valueProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                lastSelectedPrompt = newVal;
            }
        });
    }

    private void loadPrompts() {
        prompts.setAll(promptService.getAllPrompts());
        promptComboBox.setItems(prompts);

        // Выбираем последний использованный
        promptService.getLastUsedPrompt().ifPresent(last -> {
            promptComboBox.getSelectionModel().select(last);
        });
    }

    public void refreshPrompts() {
        loadPrompts();
    }

    public String getSelectedPromptText() {
        Prompt selected = promptComboBox.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getContent() != null && !selected.getContent().isEmpty()) {
            return selected.getContent();
        }
        return "";
    }

    @FXML
    private void handleManagePrompts() {
        Router.getInstance().navigateTo("prompts");
    }
}