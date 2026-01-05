package com.example.springstudy2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class Springstudy2Application {

    public static void main(String[] args) {
        SpringApplication.run(Springstudy2Application.class, args);
    }

}
