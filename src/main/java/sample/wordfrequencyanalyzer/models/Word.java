package sample.wordfrequencyanalyzer.models;

public class Word {
    private Long id;
    private String text;
    private WordType type;
    private int mentionCount;
    private boolean recorded;
    private Long groupId;
    public enum WordType {
        SINGLE, PHRASAL
    }
    public Word() {}

    public Word(String text, WordType type) {
        this.text = text;
        this.type = type;
        this.mentionCount = 1;
        this.recorded = false;
        this.groupId = null;
    }

    public Word(String text, WordType type, Long group) {
        this.text = text;
        this.type = type;
        this.mentionCount = 1;
        this.recorded = false;
        this.groupId = group;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public WordType getType() { return type; }
    public void setType(WordType type) { this.type = type; }

    public int getMentionCount() { return mentionCount; }
    public void setMentionCount(int mentionCount) { this.mentionCount = mentionCount; }

    public boolean isRecorded() { return recorded; }
    public void setRecorded(boolean recorded) { this.recorded = recorded; }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return text + " (" + mentionCount + ")";
    }




}