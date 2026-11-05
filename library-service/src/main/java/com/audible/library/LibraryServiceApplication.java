package com.audible.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class LibraryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LibraryServiceApplication.class, args);
    }
}
