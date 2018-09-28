package com.pepper.learn.netty.io.P4_AIO;

public class Server {

	public static final Integer PORT = 10010;
	
	public static void main(String[] args) {
		new Thread(new AsynTimeServerHandler(PORT),"AIO-TimeClient-001").start();
	}
	
}
