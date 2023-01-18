package ru.kich.ListsNotesBot.service;

import org.springframework.stereotype.Service;
import ru.kich.ListsNotesBot.entity.TopicEntity;
import ru.kich.ListsNotesBot.entity.UserEntity;
import ru.kich.ListsNotesBot.repository.BotRepository;

import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class BotServiceImpl implements DBBotService {

    private final BotRepository repository;

    public BotServiceImpl(BotRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<UserEntity> getAllLists() {
        return repository.findAll();
    }

    @Override
    public TopicEntity getTopicByName(long id, String name) {
        UserEntity userFromDB = repository.findById(id).orElseThrow(NotFoundException::new);
        List<TopicEntity> listOfTopics = userFromDB.getTopics();
        TopicEntity result = null;
        for (TopicEntity entity : listOfTopics) {
            if (entity.getName().equals(name)) {
                result = entity;
            }
        }
        return result;
    }

    @Override
    public UserEntity updateTopicEntity(UserEntity updateEntity) {
        return null;
    }

    @Override
    public UserEntity deleteListEntity(UserEntity deletedEntity) {
        return null;
    }

    @Override
    public UserEntity getUserById(long id) {
        return null;
    }
}
