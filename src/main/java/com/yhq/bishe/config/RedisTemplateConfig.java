package com.yhq.bishe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @program: bisheBackend-master
 * @description: 本配置文件用于解决redisTemplate序列化时乱码问题
 * @author: HenryYang
 * @create: 2023-03-18 10:06
 **/
@Configuration
public class RedisTemplateConfig {

    @Bean("redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
//      为了开发方便，一般都使用<String, Object>类型
        RedisTemplate<String, Object> template = new RedisTemplate();
//      连接工厂
        template.setConnectionFactory(factory);

//        key的序列化采用StringRedisSerializer
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
//        hash的key的序列化采用StringRedisSerializer的方式
        template.setHashKeySerializer(stringRedisSerializer);

        return template;
    }

}