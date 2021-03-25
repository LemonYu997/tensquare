package com.tensquare.notice.config;

import com.tensquare.notice.listener.SysNoticeListener;
import com.tensquare.notice.listener.UserNoticeListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//配置Rabbit
@Configuration
public class RabbitConfig {

    @Bean("sysNoticeContainer")  //记得写名字，MyWebSocketHandler中使用工具类根据名字调用Bean
    public SimpleMessageListenerContainer createSys(ConnectionFactory connectionFactory) {
        //创建监听器容器
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        //使用Channel通道监听
        container.setExposeListenerChannel(true);
        //设置自己编写的监听器
        container.setMessageListener(new SysNoticeListener());

        return container;
    }

    @Bean("userNoticeContainer")  //记得写名字，MyWebSocketHandler中使用工具类根据名字调用Bean
    public SimpleMessageListenerContainer createUser(ConnectionFactory connectionFactory) {
        //创建监听器容器
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        //使用Channel通道监听
        container.setExposeListenerChannel(true);
        //设置自己编写的监听器
        container.setMessageListener(new UserNoticeListener());

        return container;
    }
}
