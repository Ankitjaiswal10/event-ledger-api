package com.example.eventledger;

import com.example.eventledger.dto.EventRequest;
import com.example.eventledger.entity.EventType;
import com.example.eventledger.repository.EventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private EventRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    @Test
    void shouldPreventDuplicateEvents() throws Exception {

        EventRequest request = buildRequest(
                "evt-001",
                BigDecimal.valueOf(100),
                EventType.CREDIT,
                Instant.now()
        );

        mockMvc.perform(
                post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                mapper.writeValueAsString(
                                        request
                                )
                        )
        ).andExpect(status().isCreated());

        mockMvc.perform(
                post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                mapper.writeValueAsString(
                                        request
                                )
                        )
        ).andExpect(status().isConflict());
    }

    @Test
    void shouldReturnEventsSortedByTimestamp() throws Exception {

        createEvent(
                "evt-3",
                Instant.parse("2026-05-15T10:30:00Z")
        );

        createEvent(
                "evt-1",
                Instant.parse("2026-05-15T10:10:00Z")
        );

        createEvent(
                "evt-2",
                Instant.parse("2026-05-15T10:20:00Z")
        );

        mockMvc.perform(
                        get("/events")
                                .param("account", "acct-001")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(
                        jsonPath("$[0].eventId")
                                .value("evt-1")
                )
                .andExpect(
                        jsonPath("$[1].eventId")
                                .value("evt-2")
                )
                .andExpect(
                        jsonPath("$[2].eventId")
                                .value("evt-3")
                );
    }

    @Test
    void shouldCalculateBalanceCorrectly() throws Exception {

        createCustomEvent(
                "evt-credit-1",
                EventType.CREDIT,
                BigDecimal.valueOf(100)
        );

        createCustomEvent(
                "evt-credit-2",
                EventType.CREDIT,
                BigDecimal.valueOf(50)
        );

        createCustomEvent(
                "evt-debit-1",
                EventType.DEBIT,
                BigDecimal.valueOf(25)
        );

        mockMvc.perform(
                        get("/accounts/acct-001/balance")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.balance")
                                .value(125)
                );
    }

    private void createEvent(
            String eventId,
            Instant timestamp
    ) throws Exception {

        EventRequest request =
                buildRequest(
                        eventId,
                        BigDecimal.TEN,
                        EventType.CREDIT,
                        timestamp
                );

        mockMvc.perform(
                post("/events")
                        .contentType(
                                MediaType.APPLICATION_JSON
                        )
                        .content(
                                mapper.writeValueAsString(
                                        request
                                )
                        )
        );
    }

    private void createCustomEvent(
            String eventId,
            EventType type,
            BigDecimal amount
    ) throws Exception {

        EventRequest request =
                buildRequest(
                        eventId,
                        amount,
                        type,
                        Instant.now()
                );

        mockMvc.perform(
                post("/events")
                        .contentType(
                                MediaType.APPLICATION_JSON
                        )
                        .content(
                                mapper.writeValueAsString(
                                        request
                                )
                        )
        );
    }

    private EventRequest buildRequest(
            String eventId,
            BigDecimal amount,
            EventType type,
            Instant timestamp
    ) {

        EventRequest request = new EventRequest();

        request.setEventId(eventId);
        request.setAccountId("acct-001");
        request.setType(type);
        request.setAmount(amount);
        request.setCurrency("USD");
        request.setEventTimestamp(timestamp);

        return request;
    }
}