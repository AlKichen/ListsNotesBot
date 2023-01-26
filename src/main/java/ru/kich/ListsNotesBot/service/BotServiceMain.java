package ru.kich.ListsNotesBot.service;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
                log.info("command: {}", callData);
                String nameOfTopic = getNameFromData(callData);
                lastUsedTopic = nameOfTopic;
                sendMessage(chatId,
                        "Вы выбрали список: " + nameOfTopic + ".\n" +
                                "Выберите действия со списком или воспользуйтесь командой" +
                                " /show_topics чтобы ещё раз продемонстрировать все списки",
                        inlineKeyboardMaker.getInlineFunctionsOfTopic(nameOfTopic));
            } else if (callData.startsWith("/create_topic")) {
                log.info("command: {}", callData);
                listOfCommands.add("/create_topic");
                sendMessage(chatId, "Введите название списка");
            } else if (callData.startsWith("/functions_topic")) {
                if (callData.startsWith("/functions_topic_show_positions")) {
                    log.info("command: {}", callData);
                    String nameOfTopic = getNameFromData(callData);
                    lastUsedTopic = nameOfTopic;
                    showPositions(chatId, nameOfTopic);
                } else if (callData.startsWith("/functions_topic_delete")) {
                    log.info("command: {}", callData);
                    String nameOfTopic = getNameFromData(callData);
                    lastUsedTopic = nameOfTopic;
                    service.deleteAllPositionsInTopic(nameOfTopic);
                    service.deleteTopic(nameOfTopic);
                    sendMessage(chatId, "Список с названием: " + nameOfTopic + " был удален.");
                } else if (callData.startsWith("/functions_topic_edit_name_of_topic")) {
                    log.info("command: {}", callData);
                    lastUsedTopic = getNameFromData(callData);
                    listOfCommands.add(callData);
                    sendMessage(chatId, "Введите новое имя списка:");
                } else if (callData.startsWith("/functions_topic_add_position")) {
                    log.info("command: {}", callData);
                    //lastUsedTopic = getNameFromData(callData);
                    listOfCommands.add(callData);
                    sendMessage(chatId, "Введите позицию, которую хотите добавить в список:");
                }
            } else if (callData.startsWith("/list_of_positions")) {
                log.info("command: {}", callData);
                lastUsedPosition = getNameFromData(callData);
                String nameOfPosition = getNameFromData(callData);
                sendMessage(chatId,
                        "Вы выбрали позицию: " + nameOfPosition + ".\n" +
                                "Выберите действия с этой позицией или воспользуйтесь командой" +
                                " /show_positions чтобы ещё раз продемонстрировать все позиции списка",
                        inlineKeyboardMaker.getInlineFunctionsOfPositions(nameOfPosition));
            } else if (callData.startsWith("/functions_position")) {
                if (callData.startsWith("/functions_position_delete")) {
                    log.info("command: {}", callData);
                    String nameOfTopic = lastUsedTopic;
                    String nameOfPosition = getNameFromData(callData);
                    lastUsedPosition = nameOfPosition;
                    service.deletePosition(nameOfTopic, nameOfPosition);
                    sendMessage(chatId,
                            "Позиция: " + nameOfPosition + " была удалена из списка " + nameOfTopic + ".\n" +
                                    "Воспользуйтесь командой" +
                                    " /show_positions чтобы ещё раз продемонстрировать все позиции списка");
                } else if (callData.startsWith("/functions_position_rename")) {
                    log.info("command: {}", callData);
                    lastUsedPosition = getNameFromData(callData);
                    listOfCommands.add(callData);
                    sendMessage(chatId, "Введите новое имя позиции:");
                }
            }
        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            if (messageText.equals("/start")) {
                log.info("messageText: {}", messageText);
                startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                service.registerUser(chatId);
                sendMessage(chatId, "Пользователь создан и зарегистрирован в БД");
            } else if (messageText.equals("/создать_новый_список") || messageText.equals("/create_topic")) {
                log.info("messageText: {}", messageText);
                listOfCommands.add("/create_topic");
                sendMessage(chatId, "Введите название списка");
            } else if (messageText.equals("/справка")) {
                log.info("messageText: {}", messageText);
                listOfCommands.add("/справка");
                sendMessage(chatId, "Возможности бота: \n" +
                        "- создать список: /create_topic\n" +
                        "- показать имеющиеся списки: /show_topics \n" +
                        "- добавить позицию в список можно после выборе списка \n" +
                        "");
            } else if (messageText.equals("/show_topics") || messageText.equals("/показать_мои_списки")) {
                log.info("messageText: {}", messageText);
                List<TopicEntity> allTopics = service.getAllTopics(chatId);
                List<String> namesOfTopics = allTopics.stream().map(TopicEntity::getName).collect(Collectors.toList());
                InlineKeyboardMarkup inlineKeyBoard = inlineKeyboardMaker
                        .getInlineMessageButtonsPlusNewTopic("/list_of_topics%", namesOfTopics);
                sendMessage(chatId, "Выберите список:", inlineKeyBoard);
            } else if (messageText.equals("/show_positions")) {
                log.info("messageText: {}", messageText);
                String topicName = lastUsedTopic;
                showPositions(chatId, topicName);
            } else if (!messageText.startsWith("/")) {
                if (listOfCommands.get(listOfCommands.size() - 1).equals("/create_topic")) {
                    log.info("messageText: {}", messageText);
                    service.createTopic(chatId, messageText);
                    sendMessage(chatId, "Название списка создано и сохранено в БД. \n" +
                            "Воспользуйтесь командой /show_topics чтобы продемонтрировать все списки снова");
                } else if (listOfCommands.get(listOfCommands.size() - 1).startsWith("/functions_topic_edit_name_of_topic")) {
                    log.info("messageText: {}", messageText);
                    String oldName = lastUsedTopic;
                    service.editNameOfTopic(oldName, messageText);
                    sendMessage(chatId, "Название списка изменено и сохранено в БД");
                } else if (listOfCommands.get(listOfCommands.size() - 1).startsWith("/functions_topic_add_position")) {
                    log.info("messageText: {}", messageText);
                    String nameOfTopic = lastUsedTopic;
                    service.createPosition(nameOfTopic, messageText);
                    sendMessage(chatId, "Позиция добавлена в список и сохранена в БД. \n" +
                            "Воспользуйтесь командой /show_positions чтобы ещё раз продемонстрировать все позиции списка");
                } else if (listOfCommands.get(listOfCommands.size() - 1).startsWith("/functions_position_rename")) {
                    log.info("messageText: {}", messageText);
                    String nameOfTopic = lastUsedTopic;
                    String oldNamePosition = lastUsedPosition;
                    service.editNameOfPosition(nameOfTopic, oldNamePosition, messageText);
                    sendMessage(chatId, "Позиция переименована и сохранена в БД. \n" +
                            "Воспользуйтесь командой" +
                            " /show_positions чтобы ещё раз продемонстрировать все позиции списка");
                } else {
                    log.info("messageText: {}", messageText);
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
                inlineKeyboardMaker.getInlineMessageButtonsPlusNewPosition("/list_of_positions%", namesOfPositions));
        lastUsedTopic = nameOfTopic;
    }
}
