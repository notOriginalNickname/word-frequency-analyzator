package sample.wordfrequencyanalyzer.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sample.wordfrequencyanalyzer.models.Prompt;
import sample.wordfrequencyanalyzer.services.PromptService;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class PromptsController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(PromptsController.class);

    @FXML
    private ListView<Prompt> promptsListView;

    @FXML
    private VBox editPanel;

    @FXML
    private TextField nameField;

    @FXML
    private TextArea contentArea;

    private PromptService promptService;
    private ObservableList<Prompt> prompts;
    private Prompt editingPrompt;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Регистрируем себя в менеджере
        ControllerManager.getInstance().setPromptsController(this);

        promptService = new PromptService();
        prompts = FXCollections.observableArrayList();

        promptsListView.setItems(prompts);
        promptsListView.setCellFactory(lv -> new ListCell<Prompt>() {
            @Override
            protected void updateItem(Prompt item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });

        // При клике на элемент - открываем редактирование
        promptsListView.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                startEditing(newVal);
            }
        });

        loadPrompts();
    }

    public void refreshPrompts() {
        loadPrompts();
    }

    private void loadPrompts() {
        prompts.setAll(promptService.getAllPrompts());
        logger.info("Загружено {} промптов", prompts.size());
    }

    private void startEditing(Prompt prompt) {
        editingPrompt = prompt;
        nameField.setText(prompt.getName());
        contentArea.setText(prompt.getContent());
        editPanel.setVisible(true);
        editPanel.setManaged(true);
        logger.debug("Редактирование промпта: {}", prompt.getName());
    }

    private void clearEditPanel() {
        editingPrompt = null;
        nameField.clear();
        contentArea.clear();
        editPanel.setVisible(false);
        editPanel.setManaged(false);
        promptsListView.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleNewPrompt() {
        editingPrompt = new Prompt();
        nameField.clear();
        contentArea.clear();
        editPanel.setVisible(true);
        editPanel.setManaged(true);
        nameField.requestFocus();
        logger.debug("Создание нового промпта");
    }

    @FXML
    private void handleSave() {
        String name = nameField.getText().trim();
        String content = contentArea.getText().trim();

        if (name.isEmpty()) {
            showAlert("Ошибка", "Название промпта не может быть пустым");
            return;
        }

        if (editingPrompt != null) {
            editingPrompt.setName(name);
            editingPrompt.setContent(content);
            promptService.savePrompt(editingPrompt);
            logger.info("Сохранён промпт: {}", name);
        }

        loadPrompts();
        clearEditPanel();
    }

    @FXML
    private void handleCancel() {
        clearEditPanel();
        logger.debug("Отмена редактирования");
    }

    @FXML
    private void handleDelete() {
        if (editingPrompt == null || editingPrompt.getId() == null) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText(null);
        alert.setContentText("Удалить промпт \"" + editingPrompt.getName() + "\"?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            promptService.deletePrompt(editingPrompt.getId());
            logger.info("Удалён промпт: {}", editingPrompt.getName());
            loadPrompts();
            clearEditPanel();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}