package com.pepper.learn.netty.jsonserver.codeutil;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.List;

import com.pepper.learn.netty.jsonserver.entity.HttpJsonRequest;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

/**
 * 请求消息解码类
 * @author pei.nie
 *
 */
public class HttpJsonRequestDecoder extends AbstractHttpJsonDecoder<FullHttpRequest> {

	public HttpJsonRequestDecoder(Class<?> clazz) {
		super(clazz);
	}

	/**
	 * @param ctx channel上下文
	 * @param msg 消息
	 * @param out 输出集合
	 * @throws Exception
	 */
	@Override
	protected void decode(ChannelHandlerContext ctx, FullHttpRequest msg, List<Object> out) throws Exception {
		if (!msg.getDecoderResult().isSuccess()) {
			sendError(ctx, HttpResponseStatus.BAD_REQUEST);
			return;
		}
		HttpJsonRequest request = new HttpJsonRequest(msg, this.toObject(msg.content()));
		out.add(request);
	}

	/**
	 * 解码失败发送错误消息
	 */
	private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.copiedBuffer("Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8));
		response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
}