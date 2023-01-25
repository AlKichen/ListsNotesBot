package ru.kich.ListsNotesBot.service;

import ru.kich.ListsNotesBot.entity.PositionEntity;
import ru.kich.ListsNotesBot.entity.TopicEntity;
import ru.kich.ListsNotesBot.entity.UserEntity;

import java.util.List;

public interface DBBotService {
    UserEntity registerUser(Long id);
    UserEntity findUserById(Long id);
    TopicEntity createTopic(Long id, String nameOfTopic);
    TopicEntity getTopicByName(String nameOfTopic);
    PositionEntity createPosition(String nameOfTopic, String nameOfPosition);
    List<TopicEntity> getAllTopics(Long userId);
    List<PositionEntity> getPositionsByTopic(TopicEntity topic);
    List<PositionEntity> deleteAllPositionsInTopic(String nameOfTopic);
    TopicEntity deleteTopic(String nameOfTopic);
    TopicEntity editNameOfTopic(String oldName, String newName);
    PositionEntity deletePosition(String nameOfTopic, String nameOfPosition);
    PositionEntity editNameOfPosition (String nameOfTopic, String oldNamePosition, String newNamePosition);

}
