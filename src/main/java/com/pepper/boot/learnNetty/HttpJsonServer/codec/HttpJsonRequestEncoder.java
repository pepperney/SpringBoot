package com.pepper.boot.learnNetty.HttpJsonServer.codec;

import java.net.InetAddress;
import java.util.List;

import com.pepper.boot.learnNetty.HttpJsonServer.entity.HttpJsonRequest;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

/**
* 请求消息编码类
 * @author pei.nie
 */
public class HttpJsonRequestEncoder extends AbstractHttpJsonEncoder<HttpJsonRequest> {
	@Override
	protected void encode(ChannelHandlerContext ctx, HttpJsonRequest msg, List<Object> out) throws Exception {
		// (1)调用父类的toByteBuf，将业务需要发送的POJO对象Order实例通过fastJson转换为Json,然后封装为Netty的ByteBuf
		ByteBuf body = this.toByteBuf(msg.getBody());
		// (2) 如果业务侧自定义了HTTP消息头，则使用业务侧的消息头，否则在这里设置新的HTTP消息头
		FullHttpRequest request = msg.getRequest();
		if (request == null) {
			request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/do", body);
			HttpHeaders headers = request.headers();
			headers.set(HttpHeaders.Names.HOST, InetAddress.getLocalHost().getHostAddress());
			headers.set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
			headers.set(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP.toString() + ',' + HttpHeaders.Values.DEFLATE.toString());
			headers.set(HttpHeaders.Names.ACCEPT_CHARSET, "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
			headers.set(HttpHeaders.Names.ACCEPT_LANGUAGE, "zh");
			headers.set(HttpHeaders.Names.USER_AGENT, "Netty json Http Client side");
			headers.set(HttpHeaders.Names.ACCEPT, "text/html,application/json;q=0.9,*/*;q=0.8");
		}
		HttpHeaders.setContentLength(request, body.readableBytes());
		// (3)
		// 完成消息体的序列化之后将重新构造的HTTP请求消息加入到out中，由后续Netty的HTTP请求编码器继续对HTTP请求消息进行编码
		out.add(request);
	}

}