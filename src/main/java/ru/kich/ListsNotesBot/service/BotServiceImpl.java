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
    public List<TopicEntity> getTopicByName(String nameOfTopic) {
        return topicRepositiry.findByName(nameOfTopic);
    }

    @Override
    public PositionEntity createPosition(Long userId, String nameOfTopic, String nameOfPosition) {
        List<TopicEntity> topicEntityList = getTopicByName(nameOfTopic);
        PositionEntity positionEntity = new PositionEntity();
        positionEntity.setNamePosition(nameOfPosition);
        TopicEntity topic = null;
        for (TopicEntity entity : topicEntityList){
            if (entity.getUser().getId().equals(userId)){
                topic = entity;
            }
        }
        positionEntity.setTopic(topic);
        return positionRepository.save(positionEntity);
    }

    @Override
    public List<TopicEntity> getAllTopics(Long userId) {
        return topicRepositiry.findByUser_Id(userId);
    }

    @Override
    public List<PositionEntity> getPositionsByTopicId(TopicEntity topic) {
        return positionRepository.findByTopic(topic);
    }

}
