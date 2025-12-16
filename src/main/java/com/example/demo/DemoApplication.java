package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        // 这是一个有了冲突的测试
        SpringApplication.run(DemoApplication.class, args);
    }

}