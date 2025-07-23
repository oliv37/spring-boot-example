package org.springframework.boot.example.hibernate.lock.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.boot.example.hibernate.lock.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;

import static org.hibernate.cfg.AvailableSettings.JAKARTA_LOCK_TIMEOUT;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = JAKARTA_LOCK_TIMEOUT, value = "-2"))
    Player findFirstByActiveOrderByIdAsc(boolean active);
}
