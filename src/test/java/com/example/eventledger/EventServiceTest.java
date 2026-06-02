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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

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

        when(repository.existsById("evt-001"))
                .thenReturn(true);

        assertThrows(
                DuplicateEventException.class,
                () -> service.createEvent(request)
        );
    }

    @Test
    void shouldCalculateZeroBalanceWhenNoEventsExist() {

        when(
                        repository.findByAccountIdOrderByEventTimestampAsc(
                                "acct-001"))
                .thenReturn(java.util.Collections.emptyList());

        assertEquals(
                BigDecimal.ZERO,
                service.getBalance("acct-001")
        );
    }

    @Test
    void shouldCalculateBalanceCorrectly() {

        EventEntity credit1 = new EventEntity();
        credit1.setType(EventType.CREDIT);
        credit1.setAmount(BigDecimal.valueOf(100));

        EventEntity credit2 = new EventEntity();
        credit2.setType(EventType.CREDIT);
        credit2.setAmount(BigDecimal.valueOf(50));

        EventEntity debit = new EventEntity();
        debit.setType(EventType.DEBIT);
        debit.setAmount(BigDecimal.valueOf(25));

        when(
                        repository.findByAccountIdOrderByEventTimestampAsc(
                                "acct-001"))
                .thenReturn(
                        java.util.List.of(
                                credit1,
                                credit2,
                                debit
                        )
                );

        assertEquals(
                BigDecimal.valueOf(125),
                service.getBalance("acct-001")
        );
    }
}
