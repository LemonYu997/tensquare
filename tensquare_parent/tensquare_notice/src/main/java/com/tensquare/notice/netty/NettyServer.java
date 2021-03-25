package com.tensquare.notice.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

//Netty服务
public class NettyServer {
    //启动
    public void start(int port) {
        System.out.println("准备启动Netty...");
        //用于接受客户端的连接以及为已接受的连接创建子通道，一般用于服务端
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        //用来处理新连接
        EventLoopGroup boos = new NioEventLoopGroup();
        //用来处理业务逻辑，读写操作等
        EventLoopGroup worker = new NioEventLoopGroup();

        serverBootstrap.group(boos, worker)
                //.localAddress(port)                         //指定端口号
                .channel(NioServerSocketChannel.class)      //通道组件
                .childHandler(new ChannelInitializer() {    //处理器
                    //初始化
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        //请求消息解码器
                        channel.pipeline().addLast(new HttpServerCodec());
                        //将多个消息转为单一的request或者response对象
                        channel.pipeline().addLast(new HttpObjectAggregator(65536));
                        //处理WebSocket的消息事件
                        channel.pipeline().addLast(new WebSocketServerProtocolHandler("/ws"));

                        //创建自己的WebSocket处理器，就是用来编写业务逻辑的
                        MyWebSocketHandler myWebSocketHandler = new MyWebSocketHandler();
                        channel.pipeline().addLast(myWebSocketHandler);
                    }
                }).bind(port);  //绑定端口号
    }
}
