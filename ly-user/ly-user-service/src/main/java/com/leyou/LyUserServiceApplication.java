package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tk.mybatis.spring.annotation.MapperScan;

@MapperScan("com.leyou.mapper")
@SpringBootApplication
@EnableEurekaClient
public class LyUserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyUserServiceApplication.class,args);
    }
}
