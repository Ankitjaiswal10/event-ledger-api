package com.example.eventledger.service;

import com.example.eventledger.dto.EventRequest;
import com.example.eventledger.entity.EventEntity;
import com.example.eventledger.entity.EventType;
import com.example.eventledger.exception.DuplicateEventException;
import com.example.eventledger.exception.EventNotFoundException;
import com.example.eventledger.repository.EventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class EventService {

    private final EventRepository repository;
    private final ObjectMapper objectMapper;

    public EventService(EventRepository repository,
                        ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public EventEntity createEvent(EventRequest request) {

        if (repository.existsById(request.getEventId())) {

            throw new DuplicateEventException(
                    "Event already exists: "
                            + request.getEventId()
            );
        }

        try {

            EventEntity entity = new EventEntity();

            entity.setEventId(request.getEventId());
            entity.setAccountId(request.getAccountId());
            entity.setType(request.getType());
            entity.setAmount(request.getAmount());
            entity.setCurrency(request.getCurrency());
            entity.setEventTimestamp(request.getEventTimestamp());

            if (request.getMetadata() != null) {
                entity.setMetadata(
                        objectMapper.writeValueAsString(
                                request.getMetadata()
                        )
                );
            }

            return repository.save(entity);

        } catch (JsonProcessingException ex) {

            throw new RuntimeException(
                    "Failed to process metadata",
                    ex
            );
        }
    }

    public EventEntity getEvent(String eventId) {

        return repository.findById(eventId)
                .orElseThrow(() ->
                        new EventNotFoundException(
                                "Event not found: " + eventId
                        ));
    }

    public List<EventEntity> getEventsByAccount(
            String accountId) {

        return repository
                .findByAccountIdOrderByEventTimestampAsc(
                        accountId
                );
    }

    public BigDecimal getBalance(String accountId) {

        return repository
                .findByAccountIdOrderByEventTimestampAsc(
                        accountId
                )
                .stream()
                .map(event ->
                        event.getType() == EventType.CREDIT
                                ? event.getAmount()
                                : event.getAmount().negate())
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }
}
