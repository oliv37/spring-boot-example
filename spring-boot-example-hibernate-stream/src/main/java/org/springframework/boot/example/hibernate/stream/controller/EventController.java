package org.springframework.boot.example.hibernate.stream.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.example.hibernate.stream.model.Event;
import org.springframework.boot.example.hibernate.stream.repository.EventRepository;
import org.springframework.boot.example.hibernate.stream.repository.EventStreamRepository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.Consumer;
import java.util.stream.Stream;

@RestController
@AllArgsConstructor
@Slf4j
public class EventController {

    private final EventRepository eventRepository;

    private final EventStreamRepository eventStreamRepository;

    private final PlatformTransactionManager transactionManager;

    @GetMapping("/eventIds1")
    @Transactional
    public void getEventIds1(HttpServletResponse response) {
        response.setContentType("application/json");
        try (Stream<Event> eventStream = eventStreamRepository.streamEvents()) {
            eventStream
                    .map(Event::getId)
                    .peek(id -> log.debug(id.toString()))
                    .onClose(() -> write(response, "]"))
                    .forEach(new Consumer<>() {
                        private boolean isFirst = true;

                        @Override
                        public void accept(Long id) {
                            if (isFirst) {
                                write(response, "[" + id.toString());
                                isFirst = false;
                            } else {
                                write(response, "," + id.toString());
                            }
                        }
                    });
        }
    }

    @GetMapping("/eventIds2")
    public Stream<Long> getEventIds2() {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            return eventStreamRepository.streamEvents()
                    .onClose(() -> transactionManager.commit(status))
                    .map(Event::getId)
                    .peek(id -> log.debug(id.toString()));
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }
    }

    @GetMapping("/eventIds3")
    public Stream<Long> getEventIds3() {
        return eventStreamRepository.streamEventsWithoutActiveTransaction()
                .map(Event::getId)
                .peek(id -> log.debug(id.toString()));
    }

    // Running this service with -Xmx30m throws java.lang.OutOfMemoryError
    @GetMapping("/eventIdsError")
    @Transactional
    public void getEventIdsError(HttpServletResponse response) {
        response.setContentType("application/json");
        try (Stream<Event> eventStream = eventRepository.streamEvents()) {
            eventStream
                    .map(Event::getId)
                    .peek(id -> log.info(id.toString()))
                    .onClose(() -> write(response, "]"))
                    .forEach(new Consumer<>() {
                        private boolean isFirst = true;

                        @Override
                        public void accept(Long id) {
                            if (isFirst) {
                                write(response, "[" + id.toString());
                                isFirst = false;
                            } else {
                                write(response, "," + id.toString());
                            }
                        }
                    });
        }
    }

    @SneakyThrows
    private void write(HttpServletResponse response, String content) {
        response.getWriter().write(content);
    }
}
