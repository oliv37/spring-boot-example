package org.springframework.boot.example.hibernate.stream.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.springframework.boot.example.hibernate.stream.model.Event;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Objects;
import java.util.stream.Stream;

@Repository
@AllArgsConstructor
@Slf4j
public class EventStreamRepository {

    private final DataSource dataSource;

    private final SessionFactory sessionFactory;

    public Stream<Event> streamEvents() {
        Connection connection = getCurrentTransactionConnection();
        StatelessSession statelessSession = sessionFactory.openStatelessSession(connection);
        return statelessSession
                .createQuery("select e from Event e order by id asc", Event.class)
                .setReadOnly(true)
                .setCacheable(false)
                .setFetchSize(50)
                .stream()
                .onClose(statelessSession::close);
    }

    public Stream<Event> streamEventsWithoutActiveTransaction() {
        StatelessSession statelessSession = sessionFactory.openStatelessSession();
        try {
            statelessSession.beginTransaction();
            return statelessSession
                    .createQuery("select e from Event e order by id asc", Event.class)
                    .setReadOnly(true)
                    .setCacheable(false)
                    .setFetchSize(50)
                    .stream()
                    .onClose(() -> {
                        statelessSession.getTransaction().commit();
                        statelessSession.close();
                    });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            statelessSession.getTransaction().rollback();
            statelessSession.close();
            throw e;
        }
    }

    private Connection getCurrentTransactionConnection() {
        ConnectionHolder conHolder = (ConnectionHolder) TransactionSynchronizationManager.getResource(dataSource);
        return Objects.requireNonNull(conHolder).getConnection();
    }
}
