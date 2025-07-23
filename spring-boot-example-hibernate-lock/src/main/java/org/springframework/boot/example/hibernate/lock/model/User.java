package org.springframework.boot.example.hibernate.lock.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy= SEQUENCE, generator="user_id_seq")
    @SequenceGenerator(name="user_id_seq", sequenceName="user_id_seq")
    private Long id;

    @Column(nullable = false, updatable = false)
    private String name;

    @Column(nullable = false)
    private boolean active;

    @Version
    private Long version;

    public User(String name, boolean active) {
        this.name = name;
        this.active = active;
    }
}
