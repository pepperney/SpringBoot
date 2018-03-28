package com.pepper.learn.netty.jsonserver.server;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pepper.common.util.JsonUtil;
import com.pepper.learn.netty.jsonserver.entity.HttpJsonRequest;
import com.pepper.learn.netty.jsonserver.entity.HttpJsonResponse;
import com.pepper.web.model.entity.Order;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class HttpJsonServerHandler extends SimpleChannelInboundHandler<HttpJsonRequest> {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpJsonRequest msg) throws Exception {
		HttpRequest request = msg.getRequest();
		Order order = (Order) msg.getBody();
		logger.info("接收到了来自客户端的数请求<---------------------: " + JsonUtil.toJson(order));
		if(order==null){
			sendError(ctx, INTERNAL_SERVER_ERROR);
		}else{
			this.doBusiness(order);
		}
		ChannelFuture future = ctx.writeAndFlush(new HttpJsonResponse(null, order));
		if (!HttpHeaders.isKeepAlive(request)) {
			future.addListener(new GenericFutureListener<Future<? super Void>>() {
				@Override
				@SuppressWarnings("rawtypes")
				public void operationComplete(Future future) throws Exception {
					ctx.close();
				}
			});
		}
	}

	/**
	 * 处理客户端发送过来的数据
	 * @param order
	 */
	private void doBusiness(Order order) {
		order.setCost(299.99);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		if (ctx.channel().isActive()) {
			sendError(ctx, INTERNAL_SERVER_ERROR);
		}
	}

	private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.copiedBuffer("失败: " + status.toString() + "\r\n", CharsetUtil.UTF_8));
		response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
}
