package com.example.eventledger.service;

import com.example.eventledger.dto.EventRequest;
import com.example.eventledger.entity.EventEntity;
import com.example.eventledger.exception.DuplicateEventException;
import com.example.eventledger.exception.EventNotFoundException;
import com.example.eventledger.repository.EventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    public EventEntity createEvent(EventRequest request) {

        repository.findById(request.getEventId())
                .ifPresent(event -> {
                    throw new DuplicateEventException(
                            "Event already exists: " + request.getEventId()
                    );
                });

        EventEntity entity = new EventEntity();

        entity.setEventId(request.getEventId());
        entity.setAccountId(request.getAccountId());
        entity.setType(request.getType());
        entity.setAmount(request.getAmount());
        entity.setCurrency(request.getCurrency());
        entity.setEventTimestamp(request.getEventTimestamp());

        try {
            if (request.getMetadata() != null) {
                entity.setMetadata(
                        objectMapper.writeValueAsString(
                                request.getMetadata()
                        )
                );
            }
        } catch (JsonProcessingException e) {
            entity.setMetadata("{}");
        }

        return repository.save(entity);
    }

    public EventEntity getEvent(String eventId) {
        return repository.findById(eventId)
                .orElseThrow(() ->
                        new EventNotFoundException(
                                "Event not found: " + eventId
                        ));
    }

    public List<EventEntity> getEventsByAccount(String accountId) {
        return repository.findByAccountIdOrderByEventTimestampAsc(
                accountId
        );
    }

    public BigDecimal getBalance(String accountId) {

        BigDecimal balance =
                repository.calculateBalance(accountId);

        return balance == null
                ? BigDecimal.ZERO
                : balance;
    }
}
