package org.springframework.boot.example.hibernate.stream.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.example.hibernate.stream.model.Event;
import org.springframework.boot.example.hibernate.stream.repository.EventRepository;
import org.springframework.boot.example.hibernate.stream.repository.EventStreamRepository;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
class EventControllerWebMvcTest {

    @MockitoBean
    EventStreamRepository eventStreamRepository;

    @MockitoBean
    EventRepository eventRepository;

    @MockitoBean
    PlatformTransactionManager transactionManager;

    @Autowired
    MockMvc mockMvc;

    @Test
    void testGetEventIds1() throws Exception {
        // given
        when(eventStreamRepository.streamEvents())
                .thenReturn(Stream.of(createEvent(1L), createEvent(2L)));

        // when then
        this.mockMvc.perform(MockMvcRequestBuilders.get("/eventIds1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().string(equalTo("[1,2]")));
    }

    private Event createEvent(Long id) {
        Event event = new Event();
        event.setId(id);
        return event;
    }
}
