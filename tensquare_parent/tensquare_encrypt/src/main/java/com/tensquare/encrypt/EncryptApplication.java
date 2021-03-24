package com.tensquare.encrypt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableEurekaClient     //启用Eureka
@EnableZuulProxy        //开启Zuul网关
public class EncryptApplication {
    public static void main(String[] args) {
        SpringApplication.run(EncryptApplication.class, args);
    }
}
