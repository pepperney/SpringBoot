package com.pepper.boot.test;

/**
 * 变量初始化验证
 */
public class FeildInitTest {
	Window w1 = new Window(1);

	public FeildInitTest() {
		System.out.println("FeildInitTest()");
		w3 = new Window(3_3);
	}

	Window w2 = new Window(2);

	public void method() {
		System.out.println("method()");
	}

	Window w3 = new Window(3);

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		FeildInitTest house = new FeildInitTest();
		
		/**
		 * 打印结果如下
		 * window1 
		 * window2 
		 * window3 
		 * FeildInitTest()
		 * window33
		 */

	}
}

class Window {
	Window(int order) {
		System.out.println("window" + order);
	}
}