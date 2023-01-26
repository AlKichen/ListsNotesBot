package ru.kich.ListsNotesBot.config;

public enum ButtonNameEnum {
    HELP("/справка"),
    CREATE_TOPIC("/создать_новый_список"),
    SHOW_TOPICS("/показать_мои_списки");
    private final String buttonName;

    ButtonNameEnum(String buttonName) {
        this.buttonName = buttonName;
    }

    public String getButtonName() {
        return buttonName;
    }
}
