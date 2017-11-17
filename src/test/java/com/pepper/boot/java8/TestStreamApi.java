package com.pepper.boot.java8;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Test;

public class TestStreamApi {
	
	private List<Person> list = Arrays.asList(
			new Person("aaa", 18, "F", 1111.11),
			new Person("bbb", 20, "M", 2222.22),
			new Person("ccc", 22, "F", 3333.33),
			new Person("ddd", 24, "M", 4444.44),
			new Person("eee", 26, "F", 5555.55),
			new Person("aaa", 18, "F", 1111.11));
	
	// 中间操作
	@Test
	@SuppressWarnings("unused")
	public void createStream(){
		Stream<Person> stream = null;
		stream = list.stream();//串行流
		stream = list.parallelStream();//并行流
		Arrays.asList("aaa","bbb","ccc").stream();	
	}
	
	@Test
	public void test1(){
		list.stream().filter(p->"M".equals(p.getSex())).forEach(System.out::println);
		list.stream().distinct().forEach(System.out::println);;
	}
	
}
