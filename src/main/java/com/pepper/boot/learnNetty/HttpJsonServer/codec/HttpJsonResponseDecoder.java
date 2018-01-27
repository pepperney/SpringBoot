package com.pepper.boot.learnNetty.HttpJsonServer.codec;

import java.util.List;

import com.pepper.boot.learnNetty.HttpJsonServer.entity.HttpJsonResponse;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;


/**
 * 响应消息解码类
 * @author pei.nie
 *
 */
public class HttpJsonResponseDecoder extends AbstractHttpJsonDecoder<FullHttpResponse> {

	public HttpJsonResponseDecoder(Class<?> clazz) {
		super(clazz);
	}

	/**
	 * @param ctx channel上下文
	 * @param msg 消息
	 * @param out 输出集合
	 * @throws Exception
	 */
	@Override
	protected void decode(ChannelHandlerContext ctx, FullHttpResponse msg, List<Object> out) throws Exception {
		HttpJsonResponse response = new HttpJsonResponse(msg, this.toObject(msg.content()));
		out.add(response);
	}

}