package com.example.eventledger;

import com.example.eventledger.dto.EventRequest;
import com.example.eventledger.entity.EventEntity;
import com.example.eventledger.entity.EventType;
import com.example.eventledger.exception.DuplicateEventException;
import com.example.eventledger.repository.EventRepository;
import com.example.eventledger.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class EventServiceTest {

    private EventRepository repository;
    private EventService service;

    @BeforeEach
    void setup() {
        repository = org.mockito.Mockito.mock(EventRepository.class);
        service = new EventService(
                repository,
                new ObjectMapper()
        );
    }

    @Test
    void shouldThrowDuplicateEventException() {

        EventRequest request = new EventRequest();
        request.setEventId("evt-001");

        EventEntity existing = new EventEntity();
        existing.setEventId("evt-001");

        org.mockito.Mockito.when(
                        repository.findById("evt-001"))
                .thenReturn(java.util.Optional.of(existing));

        assertThrows(
                DuplicateEventException.class,
                () -> service.createEvent(request)
        );
    }

    @Test
    void shouldCalculateZeroBalanceWhenNoEventsExist() {

        org.mockito.Mockito.when(
                        repository.calculateBalance("acct-001"))
                .thenReturn(null);

        assertEquals(
                BigDecimal.ZERO,
                service.getBalance("acct-001")
        );
    }
}