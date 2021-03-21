package com.tensquare.article;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import util.IdWorker;

//启动类
@SpringBootApplication
//配置Mapper包扫描
@MapperScan("com.tensquare.article.dao")
public class ArticleApplication {
    public static void main(String[] args) {
        SpringApplication.run(ArticleApplication.class, args);
    }

    //添加idWorker
    @Bean
    public IdWorker createIdWorker() {
        //参数1是机器编号，参数2是序列号
        return new IdWorker(1, 1);
    }
}
