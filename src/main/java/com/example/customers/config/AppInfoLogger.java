package com.example.customers.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class AppInfoLogger implements ApplicationListener<ApplicationReadyEvent> {

    @Value("${spring.application.name}")
    private String appName;

    @Value("${app.message}")
    private String message;

    @Value("${server.port}")
    private String port;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        System.out.println("========================================");
        System.out.println("App: " + appName);
        System.out.println("Puerto: " + port);
        System.out.println(message);
        System.out.println("========================================");
    }
}