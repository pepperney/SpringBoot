package com.pepper.boot.test;

import java.util.ArrayDeque;
import java.util.Deque;

public class TestCollection {


	public static void main(String[] args) {
		learnArryDequeUseForQueue();
		learnArryDequeUseForStack();
	}



	/**
	 * Queue
	 * 用ArrayDeque实现队列功能
	 * @Description
	 * @author niepei
	 */
	public static void learnArryDequeUseForQueue() {
		Deque<Integer> queue = new ArrayDeque<>();
		for (int i = 0; i < 3; i++) {
			queue.addLast(i);
		}
		while (queue.isEmpty() == false) {
			System.out.println(queue.removeFirst());
		}
	}

	/**
	 * Stack
	 * 用ArrayDeque实现栈的功能
	 * @Description
	 * @author niepei
	 */
	public static void learnArryDequeUseForStack() {
		Deque<Integer> stack = new ArrayDeque<>();
		for (int i = 0; i < 3; i++) {
			stack.addLast(i);
		}
		while (stack.isEmpty() == false) {
			System.out.println(stack.removeLast());
		}
	}


}
