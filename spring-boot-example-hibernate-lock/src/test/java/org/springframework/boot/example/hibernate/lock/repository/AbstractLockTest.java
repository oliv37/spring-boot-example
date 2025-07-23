package org.springframework.boot.example.hibernate.lock.repository;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
abstract class AbstractLockTest {

    static ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlayerRepository playerRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        playerRepository.deleteAll();
    }
}
