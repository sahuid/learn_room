package com.sahuid.learnroom;

import com.sahuid.learnroom.ws.ChatEndpoint;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.sahuid.learnroom.mapper")
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class LearnRoomApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(LearnRoomApplication.class, args);
        // 给 websocket 容器注入 applicationContext
        ChatEndpoint.setApplicationContext(configurableApplicationContext);
    }

}
