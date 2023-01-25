package ru.kich.ListsNotesBot.config;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class InlineKeyboardMaker {

    public InlineKeyboardMarkup getInlineFunctionsOfTopic(String nameOfTopic) {
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        String show = "/functions_topic_show_positions%" + nameOfTopic;
        String delete = "/functions_topic_delete%" + nameOfTopic;
        String editName = "/functions_topic_edit_name_of_topic%" + nameOfTopic;
        rowList.add(getButton("Показать список", show));
        rowList.add(getButton("Удалить список", delete));
        rowList.add(getButton("Переименовать список", editName));
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getInlineMessageButtons(String prefix, List<String> stringList) {

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        for (String str : stringList) {
            rowList.add(getButton(str, prefix + str));
        }

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    private List<InlineKeyboardButton> getButton(String buttonName, String buttonCallBackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonName);
        button.setCallbackData(buttonCallBackData);
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        keyboardButtonsRow.add(button);
        return keyboardButtonsRow;
    }
}