package com.pepper.boot.learnNetty.HttpJsonServer.entity;

import io.netty.handler.codec.http.FullHttpRequest;

/**
 * 
 * @author pei.nie
 *
 */
public class HttpJsonRequest {

	private FullHttpRequest request;
	private Object body;

	public HttpJsonRequest(FullHttpRequest request, Object body) {
		this.request = request;
		this.body = body;
	}

	public final FullHttpRequest getRequest() {
		return request;
	}

	public final void setRequest(FullHttpRequest request) {
		this.request = request;
	}

	public final Object getBody() {
		return body;
	}

	public final void setBody(Object body) {
		this.body = body;
	}

	@Override
	public String toString() {
		return "HttpJsonRequest [request=" + request + ", body =" + body + "]";
	}
}