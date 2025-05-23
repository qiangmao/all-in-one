package com.example.demo.controller;

import com.example.demo.model.Message;
import com.example.demo.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final MessageService messageService;

    @Autowired
    public HelloController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello, Spring Boot!";
    }

    @GetMapping("/message")
    public Message getMessage(@RequestParam(value = "content", defaultValue = "Hello World") String content) {
        return messageService.createMessage(content);
    }
}