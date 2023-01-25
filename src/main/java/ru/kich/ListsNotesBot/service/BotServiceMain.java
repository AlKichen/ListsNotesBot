package ru.kich.ListsNotesBot.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kich.ListsNotesBot.config.BotConfig;
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
    private final List<String> listOfCommands = new ArrayList<>();
//    private final UserRepository userRepository;
//
//    private final TopicRepositiry topicRep;
//    private final PositionRepository positionRep;


    public BotServiceMain(BotConfig config, BotServiceImpl service, ReplyKeyboardMaker replyKeyboardMaker) {
        this.config = config;
        this.service = service;
        this.replyKeyboardMaker = replyKeyboardMaker;
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
                sendMessage(chatId, "Введите имя списка и через ; имя новой позиции в списке");
            } else if (messageText.equals("/show_topics")) {
                List<TopicEntity> allTopics = service.getAllTopics(chatId);
                StringBuilder sb = new StringBuilder();
                List<String> namesOfTopics = allTopics.stream().map(TopicEntity::getName).collect(Collectors.toList());
                for (String nameOfTopic : namesOfTopics) {
                    sb.append("- ").append(nameOfTopic).append(";").append("\n");
                } //TODO пока так, потом inline клавиатуру сделать нужно
                listOfCommands.add("/show_topics");
                sendMessage(chatId, sb.toString());
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

            /*switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    service.registerUser(chatId);
                    sendMessage(chatId, "Пользователь создан и зарегестрирован в БД");
                    listOfCommands.add("/start");
                    break;
                case "/create_topic":
                    service.createTopic(chatId, "Test name of Topic");
                    listOfCommands.add("/create_topic");
                    //TODO
                    sendMessage(chatId, "Название списка создано и сохранено в БД");
                    break;
                case "/create_position_in_topic":
                    service.createPosition(chatId, "Test name of Topic", "Test name of position");
                    listOfCommands.add("/create_position_in_topic");
                    sendMessage(chatId, "Позиция добавлена в список и сохранена в БД");
                    break;
                case "/show_topics":
                    List<TopicEntity> allTopics = service.getAllTopics(chatId);
                    listOfCommands.add("/show_topics");
                    sendMessage(chatId, allTopics.toString());
                    break;
                case "/show_positions":
                    List<TopicEntity> allTopics2 = service.getAllTopics(chatId);
                    TopicEntity topic = allTopics2.get(0);
                    List<PositionEntity> allPositions = service.getPositionsByTopicId(topic);
                    sendMessage(chatId, allPositions.toString());
                    break;
                default:
                    sendMessage(chatId, "Извините, данная функциональность в стадии разработки." +
                            " Спасибо за понимание!");

            }*/
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
//        message.setReplyMarkup();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
