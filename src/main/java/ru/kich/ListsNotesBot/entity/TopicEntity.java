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
    private Long Id;

    @Column(name = "name_of_topic", nullable = false)
    private String name;

    @ManyToOne
    private UserEntity user;
}
