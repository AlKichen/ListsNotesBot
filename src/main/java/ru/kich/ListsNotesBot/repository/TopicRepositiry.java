package ru.kich.ListsNotesBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kich.ListsNotesBot.entity.TopicEntity;
import ru.kich.ListsNotesBot.entity.UserEntity;

import java.util.List;

@Repository
public interface TopicRepositiry extends JpaRepository<TopicEntity, Long> {
    List<TopicEntity> findByName(String nameOfTopic);

    List<TopicEntity> findByUser_Id(Long id);
}
