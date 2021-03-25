package com.tensquare.notice.netty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tensquare.notice.config.ApplicationContextProvider;
import entity.Result;
import entity.StatusCode;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

//核心业务类：获取MQ消息，再通过WebSocket发送
//继承SimpleChannelInboundHandler，说明该类为处理类
public class MyWebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    //使用SpringMVC提供的解析json数据的工具类
    private static ObjectMapper MAPPER = new ObjectMapper();

    //存放WebSocket连接Map，根据用户id存放
    public static ConcurrentHashMap<String, Channel> userChannelMap = new ConcurrentHashMap();

    //因为该类没有加Spring相关注解，不能使用@Autowired自动注入，所以需要使用工具类获取Bean
    //从Spring容器中获取RabbitTemplate
    RabbitTemplate rabbitTemplate = ApplicationContextProvider.getApplicationContext().getBean(RabbitTemplate.class);

    //从Spring容器中获取消息监听器容器，处理订阅消息sysNotice
    SimpleMessageListenerContainer sysNoticeContainer = (SimpleMessageListenerContainer) ApplicationContextProvider.getApplicationContext().getBean("sysNoticeContainer");

    //从Spring容器中获取消息监听器容器，处理点赞消息userNotice
    SimpleMessageListenerContainer userNoticeContainer = (SimpleMessageListenerContainer) ApplicationContextProvider.getApplicationContext().getBean("userNoticeContainer");


    //用户请求WebSocket服务端，执行的方法
    //第一次请求的时候，需要建立WebSocket连接
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        //约定用户第一次请求携带的数据：{"userId":"1"}
        //获取用户请求数据并解析
        String json = msg.text();
        //解析json数据，获取用户id
        String userId = MAPPER.readTree(json).get("userId").asText();

        //第一次请求的时候，需要建立WebSocket连接
        Channel channel = userChannelMap.get(userId);
        if (channel == null) {
            //获取WebSocket连接
            channel = ctx.channel();

            //把连接放到容器中
            userChannelMap.put(userId, channel);
        }   //如果连接已存在，可以直接使用

        //值用完成新消息的提醒即可，只需要获取消息的数量
        //获取RabbitMQ的消息内容，并发送给用户
        RabbitAdmin rabbitAdmin = new RabbitAdmin(rabbitTemplate);
        //得到队列的一些参数，参数中包含消息数量
        //拼接获取队列名称，名称格式为约定好的
        String queueName = "article_subscribe_" + userId;
        Properties queueProperties = rabbitAdmin.getQueueProperties(queueName);
        //获取消息数量
        int noticeCount = 0;
        //判断Properties是否不为空
        if (queueProperties != null) {
            //如果不为空，获取消息的数量
            noticeCount = (int) queueProperties.get("QUEUE_MESSAGE_COUNT");
        }

        //------------以上获取订阅类消息，以下获取点赞类消息-------------------------

        //拼接获取队列名称，名称格式为约定好的
        String userQueueName = "article_thumbup_" + userId;
        Properties userQueueProperties = rabbitAdmin.getQueueProperties(userQueueName);
        //获取消息数量
        int userNoticeCount = 0;
        //判断Properties是否不为空
        if (userQueueProperties != null) {
            //如果不为空，获取消息的数量
            userNoticeCount = (int) userQueueProperties.get("QUEUE_MESSAGE_COUNT");
        }

        //封装返回的数据
        HashMap countMap = new HashMap();
        //订阅类消息数量
        countMap.put("sysNoticeCount", noticeCount);
        //点赞类消息数量
        countMap.put("userNoticeCount", userNoticeCount);
        Result result = new Result(true, StatusCode.OK, "查询成功", countMap);

        //把数据发送给用户
        //把result对象转换为json字符串
        channel.writeAndFlush(new TextWebSocketFrame(MAPPER.writeValueAsString(result)));

        //把消息从队列中清空，否则MQ消息监听器会再消费一次
        if (noticeCount > 0) {
            //清除队列
            rabbitAdmin.purgeQueue(queueName, true);
        }
        if (userNoticeCount > 0) {
            rabbitAdmin.purgeQueue(userQueueName, true);
        }

        //为用户的消息通知队列注册监听器，便于用户在线的时候，
        //一旦有消息，就可以主动推送给用户，不需要用户请求服务器获取数据
        sysNoticeContainer.addQueueNames(queueName);
        //为点赞消息注册监听器
        userNoticeContainer.addQueueNames(userQueueName);
    }
}
