package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class UserAuthApp {

    public static void main(String[] args) {
        System.out.println(TimeZone.getDefault());
        SpringApplication.run(UserAuthApp.class, args);
    }

}
