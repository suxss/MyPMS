package com.example.mypms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MyPmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyPmsApplication.class, args);
    }

}
