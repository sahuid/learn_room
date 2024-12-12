package com.sahuid.learnroom;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.sahuid.learnroom.mapper")
public class LearnRoomApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearnRoomApplication.class, args);
    }

}
