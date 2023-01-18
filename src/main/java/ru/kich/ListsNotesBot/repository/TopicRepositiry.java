package ru.kich.ListsNotesBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kich.ListsNotesBot.entity.TopicEntity;

@Repository
public interface TopicRepositiry extends JpaRepository<TopicEntity, Long> {
}
