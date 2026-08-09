package com.example.lab.controller;

import com.example.lab.model.MessageEntity;
import com.example.lab.service.MessageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<MessageEntity> messages = messageService.listMessages();
        model.addAttribute("messages", messages);
        return "index";
    }

    @PostMapping("/messages")
    public String submit(@RequestParam("text") String text, Model model) {
        messageService.saveAndPublish(text);
        List<MessageEntity> messages = messageService.listMessages();
        model.addAttribute("messages", messages);
        return "index";
    }
}
