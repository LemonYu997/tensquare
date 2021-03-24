package com.demo.io;

import java.net.Socket;

public class MyClient {
    public static void main(String[] args) {
        //开启5个线程
        for (int i = 0; i < 5; i++) {
            new ClientDemo().start();
        }
    }

    static class ClientDemo extends Thread {
        @Override
        public void run() {
            try {
                //建立socket连接
                Socket socket = new Socket("127.0.0.1", 8000);

                //持续发送
                while (true) {
                    //字节输出流
                    socket.getOutputStream().write("测试数据".getBytes());
                    socket.getOutputStream().flush();   //刷新

                    //每两秒发送一次
                    Thread.sleep(2000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
