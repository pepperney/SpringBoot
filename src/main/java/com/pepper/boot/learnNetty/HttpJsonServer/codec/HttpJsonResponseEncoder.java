package com.pepper.boot.learnNetty.HttpJsonServer.codec;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.List;

import com.pepper.boot.learnNetty.HttpJsonServer.entity.HttpJsonResponse;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;


/**
 * 响应消息编码类
 * @author pei.nie
 *
 */
public class HttpJsonResponseEncoder extends AbstractHttpJsonEncoder<HttpJsonResponse> {
	@Override
	protected void encode(ChannelHandlerContext ctx, HttpJsonResponse msg, List<Object> out) throws Exception {
		// 调用父类的编码方法将响应消息编码为ByteBuf
		ByteBuf body = this.toByteBuf(msg.getResult());
		FullHttpResponse response = msg.getHttpResponse();
		if (response == null) {
			response = new DefaultFullHttpResponse(HTTP_1_1, OK, body);
		} else {
			response = new DefaultFullHttpResponse(msg.getHttpResponse().getProtocolVersion(), msg.getHttpResponse().getStatus(), body);
		}
		response.headers().set(CONTENT_TYPE, "text/json");
		HttpHeaders.setContentLength(response, body.readableBytes());
		out.add(response);
	}

}