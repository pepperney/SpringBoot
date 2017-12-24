package com.pepper.boot.java8.functionInterface;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.junit.Test;

public class TestFunctionalInterface {

	@Test
	public void test_consumer() {
		Consumer<Integer> con = x -> System.out.println(x*x);
		con.accept(12);
	}

	@Test
	public void test_supplier() {
		Supplier<Integer> supp = () -> new Integer(12);
		supp.get();
	}

	@Test
	public void test_function() throws ParseException {
		Function<Date, String> supp = (date) ->{
			return  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
			};
		System.out.println(supp.apply(new Date()));
	}

	@Test
	public void test_predicate() throws ParseException {
		Predicate<Integer> pre = (x) -> x > 0;
		System.out.println(pre.test(12));
	}
}
