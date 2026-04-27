package sample.wordfrequencyanalyzer.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Router {
    private static final Logger logger = LoggerFactory.getLogger(Router.class);
    private static Router instance;
    private Stage primaryStage;
    private final Map<String, Parent> pages = new HashMap<>();
    private final Map<String, Consumer<Void>> onNavigateHandlers = new HashMap<>();

    private Router() {}

    public static Router getInstance() {
        if (instance == null) {
            instance = new Router();
        }
        return instance;
    }

    public void init(Stage stage) {
        this.primaryStage = stage;
    }

    /**
     * Загружает и сохраняет в кэш страницу
     */
    public Parent loadPage(String name, String fxmlPath) {
        try {
            if (!pages.containsKey(name)) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent page = loader.load();
                pages.put(name, page);
                logger.info("Страница '{}' загружена из {}", name, fxmlPath);
            }
            return pages.get(name);
        } catch (IOException e) {
            logger.error("Ошибка загрузки страницы " + name, e);
            return null;
        }
    }

    /**
     * Переключается на указанную страницу
     */
    public void navigateTo(String name) {
        Parent page = pages.get(name);
        if (page != null && primaryStage != null) {
            primaryStage.getScene().setRoot(page);

            // Вызываем обработчик, если он есть
            Consumer<Void> handler = onNavigateHandlers.get(name);
            if (handler != null) {
                handler.accept(null);
            }

            logger.info("Переход на страницу '{}'", name);
        } else {
            if (page == null) {
                logger.error("Страница '{}' не найдена в кэше", name);
            }
            if (primaryStage == null) {
                logger.error("primaryStage не инициализирован в Router");
            }
        }
    }


    public void clearCache() {
        pages.clear();
    }
}