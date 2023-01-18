package ru.kich.ListsNotesBot.config;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.kich.ListsNotesBot.service.BotServiceMain;

@Component
public class BotInitializer {

    private final BotServiceMain service;

    public BotInitializer(BotServiceMain service) {
        this.service = service;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(service);
        }
        catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
