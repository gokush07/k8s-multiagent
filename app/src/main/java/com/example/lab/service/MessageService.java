package com.example.lab.service;

import com.example.lab.model.MessageEntity;
import com.example.lab.repository.MessageRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public MessageService(MessageRepository messageRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.messageRepository = messageRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public MessageEntity saveAndPublish(String text) {
        MessageEntity saved = messageRepository.save(new MessageEntity(text));
        kafkaTemplate.send("lab-events", text);
        return saved;
    }

    public List<MessageEntity> listMessages() {
        return messageRepository.findAll();
    }
}
