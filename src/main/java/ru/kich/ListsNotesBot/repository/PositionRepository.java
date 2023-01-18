package ru.kich.ListsNotesBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kich.ListsNotesBot.entity.PositionEntity;

@Repository
public interface PositionRepository extends JpaRepository<PositionEntity, Long> {
}
