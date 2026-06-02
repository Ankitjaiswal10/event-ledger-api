package com.example.eventledger.controller;

import com.example.eventledger.dto.EventRequest;
import com.example.eventledger.entity.EventEntity;
import com.example.eventledger.repository.EventRepository;
import com.example.eventledger.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final EventRepository repository;

    public EventController(EventService eventService,
                           EventRepository repository) {
        this.eventService = eventService;
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<EventEntity> createEvent(
            @Valid @RequestBody EventRequest request) {

        EventEntity saved =
                eventService.createEvent(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventEntity> getEvent(
            @PathVariable String id) {

        return ResponseEntity.ok(
                eventService.getEvent(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<EventEntity>> getEvents(
            @RequestParam("account") String accountId) {

        return ResponseEntity.ok(
                eventService.getEventsByAccount(accountId)
        );
    }
}
