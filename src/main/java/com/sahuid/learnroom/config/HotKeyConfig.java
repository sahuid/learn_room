package com.sahuid.learnroom.config;

import com.jd.platform.hotkey.client.ClientStarter;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: mcj
 * @Description: hotkey 配置类
 * @DateTime: 2025/2/26 15:24
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = "hotkey")
public class HotKeyConfig {

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 本地缓存最大数量
     */
    private int caffeineSize;


    /**
     * 推送 key 的间隔时间
     */
    private long pushPeriod;

    /**
     * etcd 服务器完整地址
     */
    private String etcdServer;


    @Bean
    public void initHotKey() {
        ClientStarter.Builder builder = new ClientStarter.Builder();
        ClientStarter start = builder.setAppName(appName)
                .setCaffeineSize(caffeineSize)
                .setEtcdServer(etcdServer)
                .setPushPeriod(pushPeriod)
                .build();
        start.startPipeline();
    }
}
