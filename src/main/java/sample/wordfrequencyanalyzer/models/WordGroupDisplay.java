package sample.wordfrequencyanalyzer.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class WordGroupDisplay {
    private final SimpleStringProperty words;
    private final SimpleIntegerProperty totalMentions;
    private final boolean isGroup;
    private final Long groupId; // для групп, null для одиночных слов

    public WordGroupDisplay(String words, int totalMentions, boolean isGroup, Long groupId) {
        this.words = new SimpleStringProperty(words);
        this.totalMentions = new SimpleIntegerProperty(totalMentions);
        this.isGroup = isGroup;
        this.groupId = groupId;
    }

    public String getWords() { return words.get(); }
    public SimpleStringProperty wordsProperty() { return words; }

    public int getTotalMentions() { return totalMentions.get(); }
    public SimpleIntegerProperty totalMentionsProperty() { return totalMentions; }

    public boolean isGroup() { return isGroup; }
    public Long getGroupId() { return groupId; }
}
