package org.springframework.boot.example.testcontainers.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.example.testcontainers.model.Player;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class PlayerRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16-alpine"
    );

    @Autowired
    PlayerRepository playerRepository;

    @BeforeEach
    void setUp() {
        playerRepository.deleteAll();
    }

    @Test
    void testInsertPlayer() {
        // given
        Player player = new Player("John");

        // when
        Player result = playerRepository.save(player);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(playerRepository.count()).isEqualTo(1);
    }

    @Test
    void testFindByName() {
        // given
        playerRepository.save(new Player("John"));
        playerRepository.save(new Player("Mike"));

        // when
        List<Player> result = playerRepository.findByName("Mike");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).satisfies(p -> {
            assertThat(p.getId()).isNotNull();
            assertThat(p.getName()).isEqualTo("Mike");
        });
        assertThat(playerRepository.count()).isEqualTo(2);
    }
}
