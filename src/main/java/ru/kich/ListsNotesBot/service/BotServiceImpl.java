package ru.kich.ListsNotesBot.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kich.ListsNotesBot.entity.PositionEntity;
import ru.kich.ListsNotesBot.entity.TopicEntity;
import ru.kich.ListsNotesBot.entity.UserEntity;
import ru.kich.ListsNotesBot.repository.PositionRepository;
import ru.kich.ListsNotesBot.repository.TopicRepositiry;
import ru.kich.ListsNotesBot.repository.UserRepository;

import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
public class BotServiceImpl implements DBBotService {

    private final UserRepository userRepository;

    private final TopicRepositiry topicRepositiry;

    private final PositionRepository positionRepository;

    public BotServiceImpl(UserRepository userRepository,
                          TopicRepositiry topicRepositiry, PositionRepository positionRepository) {
        this.userRepository = userRepository;
        this.topicRepositiry = topicRepositiry;
        this.positionRepository = positionRepository;
    }

    @Override
    public UserEntity registerUser(Long id) {
        UserEntity user = new UserEntity();
        user.setId(id);
        return userRepository.save(user);
    }

    @Override
    public UserEntity findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public TopicEntity createTopic(Long id, String nameOfTopic) {
        UserEntity user = findUserById(id);
        TopicEntity topicEntity = new TopicEntity();
        topicEntity.setName(nameOfTopic);
        topicEntity.setUser(user);
        return topicRepositiry.save(topicEntity);
    }

    @Override
    public TopicEntity getTopicByName(String nameOfTopic) {
        return topicRepositiry.findByName(nameOfTopic).orElseThrow(NotFoundException::new);
    }

    @Override
    public PositionEntity createPosition(String nameOfTopic, String nameOfPosition) {
        TopicEntity topic = getTopicByName(nameOfTopic);
        PositionEntity positionEntity = new PositionEntity();
        positionEntity.setNamePosition(nameOfPosition);
        positionEntity.setTopic(topic);
        positionRepository.save(positionEntity);
        return positionEntity;
    }

    @Override
    public List<TopicEntity> getAllTopics(Long userId) {
        return topicRepositiry.findByUser_Id(userId);
    }

    @Override
    public List<PositionEntity> getPositionsByTopic(TopicEntity topic) {
        return positionRepository.findByTopic(topic);
    }

    @Override
    public List<PositionEntity> deleteAllPositionsInTopic(String nameOfTopic) {
        TopicEntity topic = getTopicByName(nameOfTopic);
        List<PositionEntity> listPositions = getPositionsByTopic(topic);
        positionRepository.deleteAll(listPositions);
        return listPositions;
    }

    @Override
    public TopicEntity deleteTopic(String nameOfTopic) {
        TopicEntity topic = getTopicByName(nameOfTopic);
        List<PositionEntity> listPositions = getPositionsByTopic(topic);
        if (listPositions.isEmpty()) {
            topicRepositiry.delete(topic);
        }
        return topic;
    }

    @Override
    public TopicEntity editNameOfTopic(String oldName, String newName) {
        TopicEntity topic = getTopicByName(oldName);
        topic.setName(newName);
        topicRepositiry.save(topic);
        return topic;
    }

    @Override
    public PositionEntity deletePosition(String nameOfTopic, String nameOfPosition) {
        TopicEntity topic = getTopicByName(nameOfTopic);
        List<PositionEntity> listPositions = getPositionsByTopic(topic);
        PositionEntity positionEntity = null;
        for (PositionEntity p : listPositions) {
            if (p.getNamePosition().equals(nameOfPosition)){
                positionEntity = p;
                positionRepository.delete(p);
            }
        }
        return positionEntity;
    }

    @Override
    public PositionEntity editNameOfPosition(String nameOfTopic, String oldNamePosition, String newNamePosition) {
        TopicEntity topic = getTopicByName(nameOfTopic);
        List<PositionEntity> listPositions = getPositionsByTopic(topic);
        PositionEntity positionEntity = null;
        for (PositionEntity p : listPositions) {
            if (p.getNamePosition().equals(oldNamePosition)){
                p.setNamePosition(newNamePosition);
                positionEntity = p;
                positionRepository.save(p);
            }
        }
        return positionEntity;
    }

}
