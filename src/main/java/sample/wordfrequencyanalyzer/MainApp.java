package sample.wordfrequencyanalyzer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sample.wordfrequencyanalyzer.controllers.MainController;
import sample.wordfrequencyanalyzer.database.DatabaseInitializer;
import sample.wordfrequencyanalyzer.utils.Router;


public class MainApp extends Application {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.getIcons().add(new Image("icon.png"));
        Router router = Router.getInstance();
        router.init(primaryStage);

        Parent mainPage = router.loadPage("main", "/fxml/pages/main-view.fxml");
        Parent statisticsPage = router.loadPage("statistics", "/fxml/pages/statistics-view.fxml");
        Parent promptsPage = router.loadPage("prompts", "/fxml/pages/prompts-view.fxml");

       try {
            // Инициализируем БД
            DatabaseInitializer.initialize();
            logger.debug("База данных инициализирована");
            logger.info("База данных инициализирована успешно в: " +
                    System.getProperty("user.home") + "\\WordAnalyzer\\vocab.db");

        } catch (Exception e) {
            logger.error("Ошибка при инициализации: {}", e.getMessage());
            e.printStackTrace();

        }


        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/pages/main-view.fxml"));
        Scene scene = new Scene(mainPage, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());


        primaryStage.setTitle("Word Frequency Analyzer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
