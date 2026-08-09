package com.example.lab.consumer;

import com.example.lab.model.MessageEntity;
import com.example.lab.repository.MessageRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {
    private final MessageRepository messageRepository;

    public MessageListener(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @KafkaListener(topics = "lab-events", groupId = "lab-group")
    public void listen(String message) {
        messageRepository.save(new MessageEntity("Consumed: " + message));
    }
}
