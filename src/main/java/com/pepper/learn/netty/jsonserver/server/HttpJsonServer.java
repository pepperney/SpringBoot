package com.pepper.learn.netty.jsonserver.server;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pepper.learn.netty.jsonserver.codeutil.HttpJsonRequestDecoder;
import com.pepper.learn.netty.jsonserver.codeutil.HttpJsonResponseEncoder;
import com.pepper.web.model.entity.Order;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class HttpJsonServer {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	
	public void run(final int port) throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
			 .channel(NioServerSocketChannel.class)
			 .childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					// 接收HttpJsonRequest，需要对应解码器 ByteBuf->FullHttpRequest-> HttpJsonRequestDecoder
					// 输出HttpJsonResponse，需要对应编码器HttpResponseEncoder->FullHttpResponse->HttpJsonResponseEncoder
					ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
					ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
					ch.pipeline().addLast("json-decoder", new HttpJsonRequestDecoder(Order.class));
					ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
					ch.pipeline().addLast("json-encoder", new HttpJsonResponseEncoder());
					ch.pipeline().addLast("jsonServerHandler", new HttpJsonServerHandler());
				}
			});
			ChannelFuture future = b.bind(new InetSocketAddress(port)).sync();
			logger.info("---------------------> HTTP服务器启动，网址是 : " + "http://localhost:" + port);
			future.channel().closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {
		int port = 8080;
		if (args.length > 0) {
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		new HttpJsonServer().run(port);
	}
}
