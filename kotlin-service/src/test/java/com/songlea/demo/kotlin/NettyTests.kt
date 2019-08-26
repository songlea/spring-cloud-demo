package com.songlea.demo.kotlin

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class NettyTests {

    /*
    NIO的通信步骤:
        ①创建ServerSocketChannel，为其配置非阻塞模式。
        ②绑定监听，配置TCP参数，录入backlog大小等。
        ③创建一个独立的IO线程，用于轮询多路复用器Selector。
        ④创建Selector，将之前创建的ServerSocketChannel注册到Selector上，并设置监听标识位SelectionKey.OP_ACCEPT。
        ⑤启动IO线程，在循环体中执行Selector.select()方法，轮询就绪的通道。
        ⑥当轮询到处于就绪状态的通道时，需要进行操作位判断，如果是ACCEPT状态，说明是新的客户端接入，则调用accept方法接收新的客户端。
        ⑦设置新接入客户端的一些参数，如非阻塞，并将其继续注册到Selector上，设置监听标识位等。
        ⑧如果轮询的通道标识位是READ，则进行读取，构造Buffer对象等。
    Netty通信的步骤：
        ①创建两个NIO线程组，一个专门用于网络事件处理（接受客户端的连接），另一个则进行网络通信的读写。
        ②创建一个ServerBootstrap对象，配置Netty的一系列参数，例如接受传出数据的缓存大小等。
        ③创建一个用于实际处理数据的类ChannelInitializer，进行初始化的准备工作，比如设置接受传出数据的字符集、格式以及实际处理数据的接口。
        ④绑定端口，执行同步阻塞方法等待服务器端启动即可。
     */
    class Server(private val port: Int) {

        // 处理方法
        fun run() {
            val bossGroup: EventLoopGroup = NioEventLoopGroup() // 用于处理服务器端接收客户端连接
            val workerGroup: EventLoopGroup = NioEventLoopGroup() // 用于网络通信(读写)

            try {
                val bootstrap = ServerBootstrap() // 用于服务器通道的一系列配置
                bootstrap.group(bossGroup, workerGroup) // 绑定两个线程组
                        .channel(NioServerSocketChannel::class.java) // 指定为NIO模式
                        .childHandler(object : ChannelInitializer<SocketChannel>() { // 配置具体的数据处理方式
                            override fun initChannel(socketChannel: SocketChannel?) {
                                socketChannel?.pipeline()?.addLast(ServerHandler())
                            }
                        })
                        /**
                        对于ChannelOption.SO_BACKLOG的解释：
                         * 服务器端TCP内核维护有两个队列，我们称之为A、B队列。客户端向服务器端connect时，会发送带有SYN标志的包（第一次握手），服务器端
                         * 接收到客户端发送的SYN时，向客户端发送SYN ACK确认（第二次握手），此时TCP内核模块把客户端连接加入到A队列中，然后服务器接收到
                         * 客户端发送的ACK时（第三次握手），TCP内核模块把客户端连接从A队列移动到B队列，连接完成，应用程序的accept会返回。也就是说accept
                         * 从B队列中取出完成了三次握手的连接。
                         * A队列和B队列的长度之和就是backlog。当A、B队列的长度之和大于ChannelOption.SO_BACKLOG时，新的连接将会被TCP内核拒绝。
                         * 所以，如果backlog过小，可能会出现accept速度跟不上，A、B队列满了，导致新的客户端无法连接。要注意的是，backlog对程序支持的
                         * 连接数并无影响，backlog影响的只是还没有被accept取出的连接
                         */
                        .option(ChannelOption.SO_BACKLOG, 128) // 设置TCP缓冲区
                        .option(ChannelOption.SO_SNDBUF, 32 * 1024) // 设置发送数据缓冲大小
                        .option(ChannelOption.SO_RCVBUF, 32 * 1024) // 设置接受数据缓冲大小
                        .childOption(ChannelOption.SO_KEEPALIVE, true) // 保持连接
                val future: ChannelFuture = bootstrap.bind(port).sync()
                future.channel().closeFuture().sync()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                workerGroup.shutdownGracefully()
                bossGroup.shutdownGracefully()
            }
        }
    }

    class ServerHandler : ChannelHandlerAdapter() {

    }

}

fun main() {
    NettyTests.Server(8379).run()
}