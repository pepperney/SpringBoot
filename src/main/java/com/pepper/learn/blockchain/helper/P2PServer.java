package com.pepper.learn.blockchain.helper;

import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;

import com.pepper.learn.blockchain.service.P2PService;

/**
 * p2p服务端
 *
 */
public class P2PServer {
	
	@Autowired
	private P2PService p2pService;
	


	public void initP2PServer(int port) {
		final WebSocketServer socketServer = new WebSocketServer(new InetSocketAddress(port)) {
			@Override
			public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
				p2pService.getSockets().add(webSocket);
			}
			@Override
			public void onClose(WebSocket webSocket, int i, String s, boolean b) {
				System.out.println("connection failed to peer:" + webSocket.getRemoteSocketAddress());
				p2pService.getSockets().remove(webSocket);
			}
			@Override
			public void onMessage(WebSocket webSocket, String msg) {
				p2pService.handleMessage(webSocket, msg, p2pService.getSockets());
			}
			@Override
			public void onError(WebSocket webSocket, Exception e) {
				System.out.println("connection failed to peer:" + webSocket.getRemoteSocketAddress());
				p2pService.getSockets().remove(webSocket);
			}
			@Override
			public void onStart() {

			}
		};
		socketServer.start();
		System.out.println("listening websocket p2p port on: " + port);
	}

}
