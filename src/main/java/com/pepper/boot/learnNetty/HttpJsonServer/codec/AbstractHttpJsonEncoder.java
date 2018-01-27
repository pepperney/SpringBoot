package com.pepper.boot.learnNetty.HttpJsonServer.codec;

import com.pepper.boot.util.JsonUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;

public abstract class AbstractHttpJsonEncoder<T> extends MessageToMessageEncoder<T> {

	/**
	 * 将业务对象序列化为json，再封装成Netty的ByteBuf
	 * @param body
	 * @return
	 */
	protected ByteBuf toByteBuf(Object body) {
		String jsonStr = JsonUtil.toJson(body);
		ByteBuf encodeBuf = Unpooled.copiedBuffer(jsonStr, CharsetUtil.UTF_8);
		return encodeBuf;
	}

}