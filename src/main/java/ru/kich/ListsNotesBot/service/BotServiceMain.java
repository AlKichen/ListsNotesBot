package ru.kich.ListsNotesBot.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kich.ListsNotesBot.config.BotConfig;
import ru.kich.ListsNotesBot.config.InlineKeyboardMaker;
import ru.kich.ListsNotesBot.config.ReplyKeyboardMaker;
import ru.kich.ListsNotesBot.entity.PositionEntity;
import ru.kich.ListsNotesBot.entity.TopicEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BotServiceMain extends TelegramLongPollingBot {

    private final BotConfig config;
    private final BotServiceImpl service;
    private final ReplyKeyboardMaker replyKeyboardMaker;
    private final InlineKeyboardMaker inlineKeyboardMaker;
    private final List<String> listOfCommands = new ArrayList<>();


    public BotServiceMain(BotConfig config, BotServiceImpl service,
                          ReplyKeyboardMaker replyKeyboardMaker,
                          InlineKeyboardMaker inlineKeyboardMaker) {
        this.config = config;
        this.service = service;
        this.replyKeyboardMaker = replyKeyboardMaker;
        this.inlineKeyboardMaker = inlineKeyboardMaker;
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            String call_data = update.getCallbackQuery().getData();
            if (call_data.startsWith("/list_of_topics")) {
                String[] arr = call_data.split("%");
                String nameOfTopic = arr[1];
                sendMessage(update.getCallbackQuery().getMessage().getChatId(),
                        "Вы выбрали список: " + nameOfTopic + ".\n" +
                                "Выберите действия со списком или воспользуйтесь командой" +
                                " /show_topics чтобы ещё раз продемонстрировать все списки",
                        inlineKeyboardMaker.getInlineFunctionsOfTopic(nameOfTopic)); //TODO функционал кнопок
            } else if (call_data.startsWith("/functions_topic")) {

            }
        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            if (messageText.equals("/start")) {
                startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                service.registerUser(chatId);
                sendMessage(chatId, "Пользователь создан и зарегистрирован в БД");
                listOfCommands.add("/start");
            } else if (messageText.equals("/create_topic")) {
                listOfCommands.add("/create_topic");
                sendMessage(chatId, "Введите название списка");
            } else if (messageText.equals("/create_position_in_topic")) {
                listOfCommands.add("/create_position_in_topic");
                //TODO видимо не нужно это - в открытом списке будет кнопка добавить поцицию
                sendMessage(chatId, "Введите имя списка и через ; имя новой позиции в списке");
            } else if (messageText.equals("/show_topics")) {
                List<TopicEntity> allTopics = service.getAllTopics(chatId);
                List<String> namesOfTopics = allTopics.stream().map(TopicEntity::getName).collect(Collectors.toList());
                InlineKeyboardMarkup inlineKeyBoard = inlineKeyboardMaker
                        .getInlineMessageButtons("/list_of_topics%", namesOfTopics);
                listOfCommands.add("/show_topics");
                sendMessage(chatId, "Выберите список:", inlineKeyBoard);
            } else if (messageText.equals("/show_positions")) {
                List<TopicEntity> allTopics2 = service.getAllTopics(chatId);
                TopicEntity topic = allTopics2.get(0);
                List<PositionEntity> allPositions = service.getPositionsByTopicId(topic);
                StringBuilder sb2 = new StringBuilder();
                List<String> namesOfPositions = allPositions.stream()
                        .map(PositionEntity::getNamePosition).collect(Collectors.toList());
                for (String nameOfPosition : namesOfPositions) {
                    sb2.append("- ").append(nameOfPosition).append(";").append("\n");
                }
                listOfCommands.add("/show_positions");
                sendMessage(chatId, sb2.toString());
            } else if (!messageText.startsWith("/")) {
                if (listOfCommands.get(listOfCommands.size() - 1).equals("/create_topic")) {
                    service.createTopic(chatId, messageText);
                    sendMessage(chatId, "Название списка создано и сохранено в БД");
                } else if (listOfCommands.get(listOfCommands.size() - 1).equals("/create_position_in_topic")) {
                    String[] arr = messageText.split(";");
                    String nameOfTopicAdded = arr[0];
                    String nameOfPositionAdded = arr[1];
                    service.createPosition(chatId, nameOfTopicAdded, nameOfPositionAdded);
                    sendMessage(chatId, "Позиция добавлена в список и сохранена в БД");
                } else {
                    sendMessage(chatId, "Извините, данная функциональность в стадии разработки." +
                            " Спасибо за понимание!");
                }
            }
        }
    }

    private void startCommandReceived(long chatId, String firstName) {
        String answer = "Привет, " + firstName + "! \nРад видеть тебя!\nСпасибо что воспользовался ListsNotesBot";
        sendMessage(chatId, answer);

    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        message.setReplyMarkup(replyKeyboardMaker.getMainMenuKeyboard());
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessage(long chatId, String textToSend, InlineKeyboardMarkup inlineKeyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        message.setReplyMarkup(replyKeyboardMaker.getMainMenuKeyboard());
        message.setReplyMarkup(inlineKeyboard);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
