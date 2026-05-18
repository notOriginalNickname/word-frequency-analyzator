package sample.wordfrequencyanalyzer.controllers;

public class ControllerManager {
    private static ControllerManager instance;
    private StatisticsController statisticsController;
    private PromptsController promptsController;

    private ControllerManager() {}

    public static ControllerManager getInstance() {
        if (instance == null) {
            instance = new ControllerManager();
        }
        return instance;
    }

    public void setStatisticsController(StatisticsController controller) {
        this.statisticsController = controller;
    }

    public void setPromptsController(PromptsController controller) {this.promptsController = controller;}

    public void refreshStatistics() {
        if (statisticsController != null) {
            statisticsController.refreshData();
        }
    }

    public void refreshPrompts() {
        if (promptsController != null) {
            promptsController.refreshPrompts();
        }
    }

}