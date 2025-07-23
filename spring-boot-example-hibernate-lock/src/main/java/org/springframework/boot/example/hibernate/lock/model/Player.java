package org.springframework.boot.example.hibernate.lock.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "players")
@Data
@NoArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy= SEQUENCE, generator="player_id_seq")
    @SequenceGenerator(name="player_id_seq", sequenceName="player_id_seq")
    private Long id;

    @Column(nullable = false, updatable = false)
    private String name;

    @Column(nullable = false)
    private boolean active;

    public Player(String name, boolean active) {
        this.name = name;
        this.active = active;
    }
}
