package com.tensquare.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

//spring-boot启动类
@SpringBootApplication
@MapperScan("com.tensquare.user.dao")   //mapper扫描
@EnableEurekaClient //启用Eureka
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
