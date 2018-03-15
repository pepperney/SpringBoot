package com.pepper.learn.netty.jsonserver.codeutil;

import com.pepper.common.util.JsonUtil;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;

public abstract class AbstractHttpJsonDecoder<T> extends MessageToMessageDecoder<T> {

	private Class<?> clazz;

	protected AbstractHttpJsonDecoder(Class<?> clazz) {
		this.clazz = clazz;
	}

	/**
	 * 从HTTP的消息体中获取请求码流，然后通过fastJson反序列化为POJO对象
	 * @param body
	 * @return
	 */
	protected Object toObject(ByteBuf body) {
		String content = body.toString(CharsetUtil.UTF_8);
		Object obj = JsonUtil.jsonToObject(content, clazz);
		return obj;
	}
}
