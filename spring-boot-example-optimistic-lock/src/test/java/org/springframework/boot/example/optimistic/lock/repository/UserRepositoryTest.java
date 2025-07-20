package org.springframework.boot.example.optimistic.lock.repository;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.example.optimistic.lock.model.User;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class UserRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16-alpine"
    );

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    UserRepository userRepository;

    ExecutorService executorService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        executorService = Executors.newVirtualThreadPerTaskExecutor();
    }

    @AfterEach
    void tearDown() {
        executorService.shutdown();
    }

    @Test
    void testInsertUser() {
        // given
        User user = new User("John", true);

        // when
        User result = userRepository.save(user);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("John");
        assertThat(result.isActive()).isTrue();
        assertThat(result.getVersion()).isZero();
    }

    @Test
    void testUpdateUser() {
        // given
        User user = userRepository.save(new User("John", true));

        // when
        user.setActive(false);
        User result = userRepository.save(user);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("John");
        assertThat(result.isActive()).isFalse();
        assertThat(result.getVersion()).isEqualTo(1);
    }

    @Test
    void testOptimisticLock() {
        // given
        User user = userRepository.save(new User("John", true));

        // and
        CountDownLatch updateUserLatch = new CountDownLatch(1);
        CountDownLatch findUserLatch = new CountDownLatch(1);

        // when
        Runnable findUser = () -> {
            new TransactionTemplate(transactionManager).executeWithoutResult((_) -> {
                User u = userRepository.findByIdWithOptimisticLock(user.getId());
                Assert.notNull(u.getId(), "user id must not be null");

                updateUserLatch.countDown();

                await(findUserLatch);
            });
        };

        // and
        Runnable updateUser = () -> {
            await(updateUserLatch);

            user.setActive(false);
            userRepository.save(user);

            findUserLatch.countDown();
        };

        // and
        Future<?> f1 = executorService.submit(findUser);
        Future<?> f2 = executorService.submit(updateUser);

        // then
        assertThatThrownBy(f1::get)
                .isInstanceOf(ExecutionException.class)
                .hasCauseInstanceOf(ObjectOptimisticLockingFailureException.class);
        assertThatNoException().isThrownBy(f2::get);

        // and
        User result = userRepository.findById(user.getId()).orElseThrow();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("John");
        assertThat(result.isActive()).isFalse();
        assertThat(result.getVersion()).isEqualTo(1);
    }

    @Test
    void testOptimisticForceIncrementLock() {
        // given
        User user = userRepository.save(new User("John", true));

        // when
        User result = new TransactionTemplate(transactionManager).execute((_) ->
                userRepository.findByIdWithOptimisticForceIncrementLock(user.getId())
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("John");
        assertThat(result.isActive()).isTrue();
        assertThat(result.getVersion()).isEqualTo(1);

        // and
        assertThat(user.getId()).isNotNull();
        assertThat(user.getName()).isEqualTo("John");
        assertThat(user.isActive()).isTrue();
        assertThat(user.getVersion()).isEqualTo(0);
    }

    @SneakyThrows
    private void await(CountDownLatch countDownLatch) {
        countDownLatch.await();
    }
}
