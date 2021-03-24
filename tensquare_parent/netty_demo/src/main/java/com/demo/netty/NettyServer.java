package com.demo.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

public class NettyServer {
    public static void main(String[] args) {
        //用于接受客户端的连接以及为已接受的连接创建子通道，一般用于服务端
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        //包含有多个EventLoop的实例，用来管理eventLoop的组件
        //可以理解为一个线程池，内部维护了一组线程
        //处理新的连接
        NioEventLoopGroup boos = new NioEventLoopGroup();
        //处理数据
        NioEventLoopGroup worker = new NioEventLoopGroup();
        //服务端执行
        serverBootstrap
                .group(boos, worker)
                //对网络套接字的I/O操作，例如读、写、连接、绑定等操作进行适配和封装的组件
                .channel(NioServerSocketChannel.class)
                //ChannelInitializer：用于对刚创建的channel进行初始化
                // 将ChannelHandler添加到channel的ChannelPipeline处理链路中
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    //初始化channel方法
                    protected void initChannel(NioSocketChannel ch) {
                        //例如一个流水线车间，当组件从流水线头部进入，穿越流水线，
                        // 流水线上的工人按顺序对组件进行加工，到达流水线尾部时商品组装完成。
                        // 流水线相当于ChannelPipeline，流水线工人相当于ChannelHandler，
                        // 源头的组件当做event
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
                            //自己指定流水线工人可以干什么事
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, String msg) {
                                System.out.println(msg);
                            }
                        });
                    }
                })
                .bind(8000);    //绑定端口
    }
}