package ru.kich.ListsNotesBot.service;

import ru.kich.ListsNotesBot.entity.PositionEntity;
import ru.kich.ListsNotesBot.entity.TopicEntity;
import ru.kich.ListsNotesBot.entity.UserEntity;

import java.util.List;

public interface DBBotService {
    UserEntity registerUser(Long id);
    UserEntity findUserById(Long id);
    TopicEntity createTopic(Long id, String nameOfTopic);
    List<TopicEntity> getTopicByName(String nameOfTopic);
    PositionEntity createPosition(Long userId, String nameOfTopic, String nameOfPosition);
    List<TopicEntity> getAllTopics(Long userId);
    List<PositionEntity> getPositionsByTopicId(TopicEntity topic);

}
