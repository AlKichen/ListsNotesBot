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
    private String lastUsedTopic;
    private String lastUsedPosition;


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
            String callData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            if (callData.startsWith("/list_of_topics")) {
                String nameOfTopic = getNameFromData(callData);
                lastUsedTopic = nameOfTopic;
                sendMessage(chatId,
                        "Вы выбрали список: " + nameOfTopic + ".\n" +
                                "Выберите действия со списком или воспользуйтесь командой" +
                                " /show_topics чтобы ещё раз продемонстрировать все списки",
                        inlineKeyboardMaker.getInlineFunctionsOfTopic(nameOfTopic));
                System.out.println(lastUsedTopic+"; "+ lastUsedPosition); //test
            } else if (callData.startsWith("/functions_topic")) {
                if (callData.startsWith("/functions_topic_show_positions")) {
                    String nameOfTopic = getNameFromData(callData);
                    lastUsedTopic = nameOfTopic;
                    showPositions(chatId, nameOfTopic);
                    System.out.println(lastUsedTopic+"; "+ lastUsedPosition); //test
                } else if (callData.startsWith("/functions_topic_delete")) {
                    String nameOfTopic = getNameFromData(callData);
                    lastUsedTopic = nameOfTopic;
                    service.deleteAllPositionsInTopic(nameOfTopic);
                    service.deleteTopic(nameOfTopic);
                    sendMessage(chatId, "Список с названием: " + nameOfTopic + " был удален.");
                    System.out.println(lastUsedTopic+"; "+ lastUsedPosition); //test
                } else if (callData.startsWith("/functions_topic_edit_name_of_topic")) {
                    lastUsedTopic = getNameFromData(callData);
                    listOfCommands.add(callData);
                    sendMessage(chatId, "Введите новое имя списка:");
                    System.out.println(lastUsedTopic+"; "+ lastUsedPosition); //test
                } else if (callData.startsWith("/functions_topic_add_position")) {
                    lastUsedTopic = getNameFromData(callData);
                    listOfCommands.add(callData);
                    sendMessage(chatId, "Введите позицию, которую хотите добавить в список:");
                    System.out.println(lastUsedTopic+"; "+ lastUsedPosition); //test
                }
            } else if (callData.startsWith("/list_of_positions")) {
                lastUsedPosition = getNameFromData(callData);
                String nameOfPosition = getNameFromData(callData);
                sendMessage(chatId,
                        "Вы выбрали позицию: " + nameOfPosition + ".\n" +
                                "Выберите действия с этой позицией или воспользуйтесь командой" +
                                " /show_positions чтобы ещё раз продемонстрировать все позиции списка",
                        inlineKeyboardMaker.getInlineFunctionsOfPositions(nameOfPosition));
                System.out.println(lastUsedTopic+"; "+ lastUsedPosition); //test
            } else if (callData.startsWith("/functions_position")) {
                if (callData.startsWith("/functions_position_delete")) {
                    String nameOfTopic = lastUsedTopic;
                    String nameOfPosition = getNameFromData(callData);
                    lastUsedPosition = nameOfPosition;
                    service.deletePosition(nameOfTopic, nameOfPosition);
                    sendMessage(chatId,
                            "Позиция: " + nameOfPosition + " была удалена из списка " + nameOfTopic + ".\n" +
                                    "Воспользуйтесь командой" +
                                    " /show_positions чтобы ещё раз продемонстрировать все позиции списка");
                    System.out.println(lastUsedTopic+"; "+ lastUsedPosition); //test
                } else if (callData.startsWith("/functions_position_rename")) {
                    lastUsedPosition = getNameFromData(callData);
                    listOfCommands.add(callData);
                    sendMessage(chatId, "Введите новое имя позиции:");
                    System.out.println(lastUsedTopic+"; "+ lastUsedPosition); //test
                }
            }
        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            if (messageText.equals("/start")) {
                startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                service.registerUser(chatId);
                sendMessage(chatId, "Пользователь создан и зарегистрирован в БД");
                System.out.println(lastUsedTopic+"; "+ lastUsedPosition); //test
            } else if (messageText.equals("/create_topic")) { //TODO подумать как переделать
                listOfCommands.add("/create_topic");
                sendMessage(chatId, "Введите название списка");
                System.out.println(lastUsedTopic+"; "+ lastUsedPosition); //test
            } else if (messageText.equals("/create_position_in_topic")) {
                listOfCommands.add("/create_position_in_topic");
                //TODO видимо не нужно это - в открытом списке будет кнопка добавить поцицию
                sendMessage(chatId, "Введите имя списка и через ; имя новой позиции в списке");
                System.out.println(lastUsedTopic+"; "+ lastUsedPosition); //test
            } else if (messageText.equals("/show_topics")) {
                List<TopicEntity> allTopics = service.getAllTopics(chatId);
                List<String> namesOfTopics = allTopics.stream().map(TopicEntity::getName).collect(Collectors.toList());
                InlineKeyboardMarkup inlineKeyBoard = inlineKeyboardMaker
                        .getInlineMessageButtons("/list_of_topics%", namesOfTopics);
        //?        listOfCommands.add("/show_topics");
                sendMessage(chatId, "Выберите список:", inlineKeyBoard);
                System.out.println(lastUsedTopic+"; "+ lastUsedPosition); //test
            } else if (messageText.equals("/show_positions")) {
                String topicName = lastUsedTopic;
                showPositions(chatId, topicName);
                System.out.println(lastUsedTopic+"; "+ lastUsedPosition); //test
            } else if (!messageText.startsWith("/")) {
                if (listOfCommands.get(listOfCommands.size() - 1).equals("/create_topic")) {
                    service.createTopic(chatId, messageText);
                    sendMessage(chatId, "Название списка создано и сохранено в БД");
                    System.out.println(lastUsedTopic+"; "+ lastUsedPosition); //test
                } else if (listOfCommands.get(listOfCommands.size() - 1).startsWith("/functions_topic_edit_name_of_topic")) {
                    String oldName = lastUsedTopic;
                    service.editNameOfTopic(oldName, messageText);
                    sendMessage(chatId, "Название списка изменено и сохранено в БД");
                    System.out.println(lastUsedTopic+"; "+ lastUsedPosition); //test
                } else if (listOfCommands.get(listOfCommands.size() - 1).startsWith("/functions_topic_add_position")) {
                    String nameOfTopic = lastUsedTopic;
                    service.createPosition(nameOfTopic, messageText);
                    sendMessage(chatId, "Позиция добавлена в список и сохранена в БД");
                    System.out.println(lastUsedTopic+"; "+ lastUsedPosition); //test
                } else if (listOfCommands.get(listOfCommands.size() - 1).startsWith("/functions_position_rename")) {
                    String nameOfTopic = lastUsedTopic;
                    String oldNamePosition = lastUsedPosition;
                    service.editNameOfPosition(nameOfTopic, oldNamePosition, messageText);
                    sendMessage(chatId, "Позиция переименована и сохранена в БД. \n"+
                                    "Воспользуйтесь командой" +
                            " /show_positions чтобы ещё раз продемонстрировать все позиции списка");
                    System.out.println(lastUsedTopic+"; "+ lastUsedPosition); //test
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

    private String getNameFromData(String callData) {
        String[] arr = callData.split("%");
        return arr[1];
    }

    private void showPositions(long chatId, String nameOfTopic) {
        TopicEntity topic2 = service.getTopicByName(nameOfTopic);
        List<PositionEntity> allPositions = service.getPositionsByTopic(topic2);
        List<String> namesOfPositions = allPositions.stream()
                .map(PositionEntity::getNamePosition).collect(Collectors.toList());
        sendMessage(chatId, "Позиции списка " + nameOfTopic + " :",
                inlineKeyboardMaker.getInlineMessageButtons("/list_of_positions%", namesOfPositions));
    }
}
