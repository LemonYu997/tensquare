package com.tensquare.notice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.tensquare.notice.netty.MyWebSocketHandler;
import entity.Result;
import entity.StatusCode;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;

import java.util.HashMap;

//自己写的系统消息监听器
public class SysNoticeListener implements ChannelAwareMessageListener {

    //使用SpringMVC提供的解析json数据的工具类
    private static ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        //获取用户id，可以通过队列名称获取
        String queueName = message.getMessageProperties().getConsumerQueue();
        //队列名为article_subscribe_xxx，xxx为用户id，所以截取最后一个"_"之后的字符串
        String userId = queueName.substring(queueName.lastIndexOf("_") + 1);

        //获取连接通道
        io.netty.channel.Channel wsChannel = MyWebSocketHandler.userChannelMap.get(userId);

        //判断用户是否在线
        if (wsChannel != null) {
            //如果连接不为空，表示用户在线
            //封装返回数据
            HashMap countMap = new HashMap();
            countMap.put("sysNoticeCount", 1);
            Result result = new Result(true, StatusCode.OK, "查询成功", countMap);

            //把数据通过WebSocket连接主动推送用户，将对象转为json字符串
            wsChannel.writeAndFlush(new TextWebSocketFrame(MAPPER.writeValueAsString(result)));
        }
    }
}
