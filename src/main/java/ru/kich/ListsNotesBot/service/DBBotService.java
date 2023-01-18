package ru.kich.ListsNotesBot.service;

import ru.kich.ListsNotesBot.entity.TopicEntity;
import ru.kich.ListsNotesBot.entity.UserEntity;

import java.util.List;

public interface DBBotService {

    List<UserEntity> getAllLists();

    TopicEntity getTopicByName(long id, String name);

    UserEntity updateTopicEntity(UserEntity updateEntity);

    UserEntity deleteListEntity(UserEntity deletedEntity);

    UserEntity getUserById(long id);
}
