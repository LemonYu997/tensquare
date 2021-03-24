package com.demo.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

public class NettyClient {
    public static void main(String[] args) throws InterruptedException {
        //不接受新的连接，并且是在父通道类完成一些操作，一般用于客户端
        Bootstrap bootstrap = new Bootstrap();

        //包含有多个EventLoop的实例，用来管理eventLoop的组件，可以理解为一个线程池，内部维护了一组线程
        NioEventLoopGroup group = new NioEventLoopGroup();

        //客户端执行
        bootstrap.group(group)
                // Channel对网络套接字的I/O操作，
                // 例如读、写、连接、绑定等操作进行适配和封装的组件。
                .channel(NioSocketChannel.class)
                //用于对刚创建的channel进行初始化，
                //将ChannelHandler添加到channel的ChannelPipeline处理链路中。
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        // 组件从流水线头部进入，流水线上的工人按顺序对组件进行加工
                        // 流水线相当于ChannelPipeline
                        // 流水线工人相当于ChannelHandler
                        ch.pipeline().addLast(new StringEncoder());
                    }
                });
        //客户端连接服务端
        Channel channel = bootstrap.connect("127.0.0.1", 8000).channel();

        while (true) {
            // 客户端使用writeAndFlush方法向服务端发送数据，返回的是ChannelFuture
            // 与jdk中线程的Future接口类似，即实现并行处理的效果
            // 可以在操作执行成功或失败时自动触发监听器中的事件处理方法。
            channel.writeAndFlush("测试数据");
            Thread.sleep(2000);
        }
    }
}