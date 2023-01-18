package ru.kich.ListsNotesBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kich.ListsNotesBot.entity.UserEntity;

@Repository
public interface BotRepository extends JpaRepository<UserEntity, Long> {
}
