package org.springframework.boot.example.hibernate.lock.repository;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.example.hibernate.lock.model.Player;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class SkipLockTest extends AbstractLockTest {

    @Test
    void testSkipLock() {
        // given
        int nbPlayers = 50;

        // and
        playerRepository.saveAll(
                IntStream.range(1, nbPlayers + 1)
                        .mapToObj(i -> new Player("Name" + i, false))
                        .toList()
        );

        // and
        Runnable updateInactivePlayer = () -> {
            new TransactionTemplate(transactionManager).executeWithoutResult((_) -> {
                Player player = playerRepository.findFirstByActiveOrderByIdAsc(false);
                log.info("Update player {}", player.getId());
                sleep(1_000);
                player.setActive(true);
            });
        };

        // when
        CompletableFuture.allOf(
                IntStream.range(0, nbPlayers)
                        .mapToObj(_ -> CompletableFuture.runAsync(updateInactivePlayer, executorService))
                        .toArray(CompletableFuture[]::new)
        ).join();

        // then
        assertThat(playerRepository.findAll()).allMatch(Player::isActive);
    }

    @SneakyThrows
    private void sleep(long millis) {
        Thread.sleep(millis);
    }
}
