package ru.kich.ListsNotesBot.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "topics")
@Data
public class TopicEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long topicId;
    @Column(name = "name_of_topic", nullable = false)
    private String name;

    @OneToMany(mappedBy = "topic")
    private List<PositionEntity> listInTopicEntities;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
