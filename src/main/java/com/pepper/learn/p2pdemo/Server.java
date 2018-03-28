package com.pepper.learn.p2pdemo;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

/**
 * p2p服务端
 * 
 * @author pei.nie
 *
 */
public class Server {
	
	private List<WebSocket> sockets = new ArrayList<WebSocket>();
	
	public List<WebSocket> getSockets() {
		return sockets;
	}
	
	public void initP2PServer(int port) {
		final WebSocketServer socketServer = new WebSocketServer(new InetSocketAddress(port)) {
			@Override
			public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
				write(webSocket, "服务端连接成功");
				sockets.add(webSocket);
			}

			@Override
			public void onClose(WebSocket webSocket, int i, String s, boolean b) {
				System.out.println("connection failed to peer:" + webSocket.getRemoteSocketAddress());
				sockets.remove(webSocket);
			}

			@Override
			public void onMessage(WebSocket webSocket, String msg) {
				System.out.println("接收到客户端消息：" + msg);
				write(webSocket, "服务器收到消息");
				//broatcast("服务器收到消息:" + msg);
			}

			@Override
			public void onError(WebSocket webSocket, Exception e) {
				System.out.println("connection failed to peer:" + webSocket.getRemoteSocketAddress());
				sockets.remove(webSocket);
			}

			@Override
			public void onStart() {

			}
		};
		socketServer.start();
		System.out.println("listening websocket p2p port on: " + port);
	}
	
	public void write(WebSocket ws, String message) {
		System.out.println("发送给" + ws.getRemoteSocketAddress().getPort() + "的p2p消息:" + message);
		ws.send(message);
	}
	
	public void broatcast(String message) {
		if (sockets.size() == 0) {
			return;
		}
		System.out.println("======广播消息开始：");
		for (WebSocket socket : sockets) {
			this.write(socket, message);
		}
		System.out.println("======广播消息结束");
	}

}
