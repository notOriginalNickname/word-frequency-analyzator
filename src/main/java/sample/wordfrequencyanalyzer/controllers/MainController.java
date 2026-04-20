package sample.wordfrequencyanalyzer.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import sample.wordfrequencyanalyzer.services.MainService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private TextArea inputArea;
    @FXML
    private Label resultLabel;

    private MainService mainService;
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        mainService = new MainService();

    }
    @FXML
    private void handleProcessWords() {
        mainService.processWordsFromPage(inputArea, resultLabel);
    }

    @FXML
    private void handleClear() {
        logger.debug("handleClear вызван");
        inputArea.clear();
        resultLabel.setText("Окно очищено");
    }


}