package ru.kich.ListsNotesBot.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class UserEntity {
    @Id
    @NotNull
    @Column(name = "id", nullable = false)
    Long id;
}
