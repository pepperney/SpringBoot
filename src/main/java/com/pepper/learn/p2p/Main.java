package com.pepper.learn.p2p;

/**
 * 区块链节点启动入口
 * 
 * @author pei.nie
 *
 */
public class Main {
	public static void main(String[] args) {
		Server p2pServer = new Server();
		Client p2pClient = new Client();
		int p2pPort = Integer.valueOf(args[0]);
		// 启动p2p服务端
		p2pServer.initP2PServer(p2pPort);
		if (args.length == 2 && args[1] != null) {
			// 作为p2p客户端连接p2p服务端
			p2pClient.connectToPeer(args[1]);
		}
	}
}
