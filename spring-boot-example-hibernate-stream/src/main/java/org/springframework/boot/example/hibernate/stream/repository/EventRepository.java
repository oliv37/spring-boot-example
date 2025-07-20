package org.springframework.boot.example.hibernate.stream.repository;

import jakarta.persistence.QueryHint;
import org.springframework.boot.example.hibernate.stream.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.stream.Stream;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("select e from Event e order by id")
    @QueryHints({
            @QueryHint(name = "org.hibernate.readOnly", value = "true"),
            @QueryHint(name = "org.hibernate.cacheable", value = "false"),
            @QueryHint(name = "org.hibernate.fetchSize", value = "50"),
    })
    Stream<Event> streamEvents();
}
