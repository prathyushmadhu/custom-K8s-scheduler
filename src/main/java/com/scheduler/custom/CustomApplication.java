package com.scheduler.custom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.scheduler.custom.service.SchedulerService;

@SpringBootApplication
public class CustomApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomApplication.class, args);
    }

    @Bean
    public SchedulerService schedulerService() {
        try {
            return new SchedulerService();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize SchedulerService", e);
        }
    }
}