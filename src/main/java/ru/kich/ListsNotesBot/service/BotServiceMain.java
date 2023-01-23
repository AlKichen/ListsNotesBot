package ru.kich.ListsNotesBot.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kich.ListsNotesBot.config.BotConfig;
import ru.kich.ListsNotesBot.entity.PositionEntity;
import ru.kich.ListsNotesBot.entity.TopicEntity;
import ru.kich.ListsNotesBot.entity.UserEntity;
import ru.kich.ListsNotesBot.repository.UserRepository;
import ru.kich.ListsNotesBot.repository.PositionRepository;
import ru.kich.ListsNotesBot.repository.TopicRepositiry;

import java.util.List;

@Component
public class BotServiceMain extends TelegramLongPollingBot {

    private final BotConfig config;

    private final BotServiceImpl service;
//    private final UserRepository userRepository;
//
//    private final TopicRepositiry topicRep;
//    private final PositionRepository positionRep;


    public BotServiceMain(BotConfig config, BotServiceImpl service) {
        this.config = config;
        this.service = service;
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
            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    service.registerUser(chatId);
                    sendMessage(chatId, "Пользователь создан и зарегестрирован в БД");
                    break;
                case "/create_topic":
                    service.createTopic(chatId, "Test name of Topic");
                    sendMessage(chatId, "Название списка создано и сохранено в БД");
                    break;
                case "/create_position_in_topic":
                    service.createPosition(chatId, "Test name of Topic", "Test name of position");
                    sendMessage(chatId, "Позиция добавлена в список и сохранена в БД");
                    break;
                case "/show_topics":
                    List<TopicEntity> allTopics = service.getAllTopics(chatId);
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

            }
        }
    }

    private void startCommandReceived(long chatId, String firstName) {
        String answer = "Привет, " + firstName + "! Рад видеть тебя! Спасибо что воспользовался ListsNotesBot";
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
//        message.setReplyMarkup(replyKeyboardMaker.getMainMenuKeyboard());

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
