package com.tensquare.user.config;

import com.baomidou.mybatisplus.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//注入PaginationInterceptor插件
@Configuration
public class MyBatisPlusConfig {

    @Bean
    public PaginationInterceptor creatPaginationInterceptor() {
        return new PaginationInterceptor();
    }
}
