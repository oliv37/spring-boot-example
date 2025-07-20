package org.springframework.boot.example.testcontainers.repository;

import org.springframework.boot.example.testcontainers.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findByName(String name);
}
