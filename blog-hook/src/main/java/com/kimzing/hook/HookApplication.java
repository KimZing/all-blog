package com.kimzing.hook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class HookApplication {

    public static void main(String[] args) {
        SpringApplication.run(HookApplication.class, args);
    }

}
