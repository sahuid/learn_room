package com.sahuid.learnroom;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.sahuid.learnroom.mapper")
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class LearnRoomApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearnRoomApplication.class, args);
    }

}
