package ru.kich.ListsNotesBot.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kich.ListsNotesBot.config.BotConfig;
import ru.kich.ListsNotesBot.entity.TopicEntity;
import ru.kich.ListsNotesBot.entity.UserEntity;
import ru.kich.ListsNotesBot.repository.BotRepository;
import ru.kich.ListsNotesBot.repository.PositionRepository;
import ru.kich.ListsNotesBot.repository.TopicRepositiry;

@Component
public class BotService extends TelegramLongPollingBot {

    private final BotConfig config;
    private final BotRepository botRepository;

    private final TopicRepositiry topicRep;
    private final PositionRepository positionRep;


    public BotService(BotConfig config, BotRepository botRepository,
                      TopicRepositiry topicRep, PositionRepository positionRep) {
        this.config = config;
        this.botRepository = botRepository;
        this.topicRep = topicRep;
        this.positionRep = positionRep;
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
                    UserEntity userEntity = new UserEntity();
                    userEntity.setId(chatId);
                    botRepository.save(userEntity);
                    sendMessage(chatId, "Пользователь создан и зарегестрирован в БД");
                    break;
                case "/create_topic":
                    UserEntity user = botRepository.findById(chatId).get();
                    TopicEntity topicEntity = new TopicEntity();
                    topicEntity.setName("testTopic");
                    topicEntity.setUser(user);
                    topicRep.save(topicEntity);
                    sendMessage(chatId, "Название списка создано и сохранено в БД");
                    break;
//                case "/create_position_in_topic":
//                    UserEntity user2 = botRepository.findById(String.valueOf(chatId)).get();
//                    TopicEntity topicEntity1 = user2.getTopics().get(0);
//                    PositionEntity positionEntity = new PositionEntity();
//                    positionEntity.setNamePosition("testPosition");
//                    topicEntity1.setListInTopicEntities(List.of(positionEntity));
//                    botRepository.save(user2);
//                    sendMessage(chatId, "Позиция добавлена в список и сохранена в БД");
//                    break;
//                case "/show":
//                    UserEntity user3 = botRepository.findById(String.valueOf(chatId)).get();
//                    TopicEntity topicEntity2 = user3.getTopics().get(0);
//                    sendMessage(chatId, topicEntity2.toString());
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
