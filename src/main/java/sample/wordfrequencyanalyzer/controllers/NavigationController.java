package sample.wordfrequencyanalyzer.controllers;


import javafx.fxml.FXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sample.wordfrequencyanalyzer.utils.Router;


public class NavigationController {
    private static final Logger logger = LoggerFactory.getLogger(NavigationController.class);

    private Router router;

    @FXML
    public void initialize() {
        router = Router.getInstance();
    }

    @FXML
    private void handleShowStatistics() {
        logger.info("Переход на страницу статистики");
        router.navigateTo("statistics");
        ControllerManager.getInstance().refreshStatistics();
    }

    @FXML
    private void handleMain() {
        logger.info("Переход на главную страницу");
        router.navigateTo("main");
    }

    @FXML
    private void handleGoToPrompts() {
        logger.info("Переход на страницу промптов");
        router.navigateTo("prompts");
        // Обновляем список промптов при переходе
        ControllerManager.getInstance().refreshPrompts();
    }



}