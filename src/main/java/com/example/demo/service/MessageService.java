package com.example.demo.service;

import com.example.demo.model.Message;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    public Message createMessage(String content) {
        return new Message(content);
    }
}