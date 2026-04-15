module sample.wordfrequencyanalyzer {
    // Экспортируем пакеты для JavaFX
    exports sample.wordfrequencyanalyzer;
    exports sample.wordfrequencyanalyzer.controllers;
    exports sample.wordfrequencyanalyzer.models;
    exports sample.wordfrequencyanalyzer.dao;
    exports sample.wordfrequencyanalyzer.services;
    exports sample.wordfrequencyanalyzer.database;

    // Открываем пакеты для рефлексии (нужно для FXML)
    opens sample.wordfrequencyanalyzer.controllers to javafx.fxml;
    opens sample.wordfrequencyanalyzer.models to javafx.base;

    // Требуем необходимые модули
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires java.logging;
    requires org.slf4j;
    requires org.apache.opennlp.tools;
}