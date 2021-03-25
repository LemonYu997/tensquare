package com.tensquare.notice.config;

import com.tensquare.notice.netty.NettyServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//配置Netty服务
@Configuration
public class NettyConfig {
    @Bean
    public NettyServer createNettyServer() {
        NettyServer nettyServer = new NettyServer();

        //启动NettyServer，使用新的线程启动
        new Thread() {
            @Override
            public void run() {
                nettyServer.start(1234);
            }
        }.start();

        return  nettyServer;
    }
}
