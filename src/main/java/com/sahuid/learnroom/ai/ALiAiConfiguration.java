package com.sahuid.learnroom.ai;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: mcj
 * @Description: Ai 模块的 bean 管理
 * @DateTime: 2025/2/5 14:09
 **/
@Configuration
public class ALiAiConfiguration {

    @Bean
    public Generation generation() {
        return new Generation();
    }

    @Bean
    public Message systemMessage() {
        return Message.builder()
                .role(Role.SYSTEM.getValue())
                .content("You are a helpful assistant.")
                .build();
    }
}
