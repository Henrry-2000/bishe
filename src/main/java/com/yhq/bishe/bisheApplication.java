package com.yhq.bishe;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.yhq.bishe.mapper")
@EnableScheduling
public class bisheApplication {

    public static void main(String[] args) {
        SpringApplication.run(bisheApplication.class, args);
    }

}
