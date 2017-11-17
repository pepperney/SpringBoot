package com.pepper.boot.java8;
import org.junit.Test;


public class TestLambda {
	
	
	@Test
	public void test1(){
		
		Compute com = null;
		
		// 1.匿名内部类方式
		com = new Compute() {
			@Override
			public Integer doCompute(Integer arg1, Integer arg2) {
				return arg1+arg2;
			}
		};
		System.out.println(com.doCompute(12, 3));
		
		// 2.lambda表达式 -- 2入参，2条语句，有返回值
		com = (Integer x, Integer y) -> {
			System.out.println(x + y);
			return x + y;
		};
		System.out.println(com.doCompute(12, 3));
		
		// 3.lambda表达式 -- 2入参，2条语句，有返回值 --省略入参类型
		com = (x, y) -> {
			System.out.println(x + y);
			return x + y;
		};
		System.out.println(com.doCompute(12, 3));
		
		// 4.lambda表达式 -- 2入参，1条语句，有返回值 --省略入参类型，省略方法体括号，省略return关键字
		com = (x, y) ->  x + y;;
		System.out.println(com.doCompute(12, 3));
		
		// 5.lambda表达式 -- 2入参，1条语句，有返回值 --省略入参类型，省略方法体括号
		com = (x, y) -> {
			System.out.println(x + y);
			return x + y;
		};
		System.out.println(com.doCompute(12, 3));
			
		// 6.lambda表达式 -- 2入参，1条语句，有返回值 --省略入参类型，省略方法体括号
		com = (x, y) -> {
			System.out.println(x + y);
			return x + y;
		};
		System.out.println(com.doCompute(12, 3));
		
		// 7.lambda表达式 -- 1入参，1条语句，有返回值 --省略入参类型，省略入参括号，省略方法体括号，省略return关键字
		ComputeSquare cs = x ->  x*x;	
		System.out.println(cs.doComputeSquare(12));
		
		// 8.lambda表达式-- 无入参，1条语句，无返回值
		Runnable r = () -> System.out.println("Hello World");
		new Thread(r).start();
	}
	
	
	
	
	
	
	
	
}
