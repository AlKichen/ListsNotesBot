package ru.kich.ListsNotesBot.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Table(name = "positions")
public class PositionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    private Long Id;

    private String namePosition;

    @ManyToOne
    private TopicEntity topic;

}
