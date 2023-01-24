package ru.kich.ListsNotesBot.config;

public enum ButtonNameEnum {
    START("/start"),
    CREATE_TOPIC("/create_topic"),
    CREATE_POSITION_IN_TOPIC("/create_position_in_topic"),
    SHOW_TOPICS("/show_topics"),
    SHOW_POSITIONS("/show_positions");
    private final String buttonName;

    ButtonNameEnum(String buttonName) {
        this.buttonName = buttonName;
    }

    public String getButtonName() {
        return buttonName;
    }
}
