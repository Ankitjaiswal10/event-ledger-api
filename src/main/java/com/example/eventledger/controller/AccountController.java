package com.example.eventledger.controller;

import com.example.eventledger.dto.BalanceResponse;
import com.example.eventledger.service.EventService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final EventService eventService;

    public AccountController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/{accountId}/balance")
    public BalanceResponse getBalance(
            @PathVariable String accountId) {

        return new BalanceResponse(
                accountId,
                eventService.getBalance(accountId)
        );
    }
}
