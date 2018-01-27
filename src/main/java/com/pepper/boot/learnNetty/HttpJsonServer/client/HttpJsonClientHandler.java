package com.pepper.boot.learnNetty.HttpJsonServer.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pepper.boot.learnNetty.HttpJsonServer.entity.HttpJsonRequest;
import com.pepper.boot.model.entity.Order;
import com.pepper.boot.model.entity.UserInfo;
import com.pepper.boot.util.JsonUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class HttpJsonClientHandler extends ChannelInboundHandlerAdapter {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("连接上服务器--------------------->");
		Order reqBody = this.createReqBody();// 构造请求body内容
		HttpJsonRequest request = new HttpJsonRequest(null, reqBody);
		ctx.writeAndFlush(request);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		logger.info("接收到了来自服务端的数据<---------------------" + JsonUtil.toJson(msg));
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.info("客户端异常",cause);
		ctx.close();
	}

	private Order createReqBody() {
		Order order = new Order();
		UserInfo user = new UserInfo();
		user.setUserId(1);
		user.setUserName("pepper");
		user.setMobile("15500000000");
		user.setSex("男");
		user.setAge(25);
		order.setOrderId(1);
		order.setCustomer(user);
		return order;
	}
}