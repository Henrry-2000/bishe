package com.yhq.bishe.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.api.RedissonRxClient;
import org.redisson.config.Config;
import org.redisson.api.RedissonClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * @program: bisheBackend-master
 * @description: redisson的配置文件
 * @author: HenryYang
 * @create: 2023-03-19 10:29
 **/
@Configuration
//读取yml中已有的配置
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {
    private String port ;
    private String host ;
    @Bean
    public RedissonClient redissonClient(){
        // 1. 创建配置信息
        Config config = new Config();
        String redisAddress = String.format("redis://%s:%s",host,port);
        config.useSingleServer().setAddress(redisAddress).setDatabase(2);

        // 2. 创建实例
        // Sync and Async API
        RedissonClient redisson = Redisson.create(config);
        return redisson;

    }
}