package com.example.lab.controller;

import com.example.lab.service.MessageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    private final MessageService messageService;

    public HealthController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/health")
    public String health() {
        return "ok";
    }
}
