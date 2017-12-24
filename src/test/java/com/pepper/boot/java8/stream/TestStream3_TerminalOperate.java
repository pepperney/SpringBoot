package com.pepper.boot.java8.stream;

import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import com.pepper.boot.java8.stream.Employee.Status;
/**
 * 终止操作
 * @author pei.nie
 *
 */
public class TestStream3_TerminalOperate {
	
	List<Employee> emps = Arrays.asList(
			new Employee(102, "李四", 59, 6666.66, Status.BUSY),
			new Employee(101, "张三", 18, 9999.99, Status.FREE),
			new Employee(103, "王五", 28, 3333.33, Status.VACATION),
			new Employee(104, "赵六", 8, 7777.77, Status.BUSY),
			new Employee(104, "赵六", 8, 7777.77, Status.FREE),
			new Employee(104, "赵六", 8, 7777.77, Status.FREE),
			new Employee(105, "田七", 38, 5555.55, Status.BUSY)
	);
	
	/**
	 * 1.查找与匹配
	 * allMatch  —— 检查是否匹配所有元素 
	 * anyMatch	 —— 检查是否至少匹配一个元素 
	 * noneMatch —— 检查是否没有匹配的元素
	 * findFirst —— 返回第一个元素 
	 * findAny	 —— 返回当前流中的任意元素 
	 * count	 —— 返回流中元素的总个数 
	 * max		 —— 返回流中最大值
	 * min		 —— 返回流中最小值
	 */
	@Test
	public void test_match(){
			boolean bl = emps.stream().allMatch((e) -> e.getStatus().equals(Status.BUSY));
			System.out.println(bl);
			
			boolean bl1 = emps.stream().anyMatch((e) -> e.getStatus().equals(Status.BUSY));
			System.out.println(bl1);
			
			boolean bl2 = emps.stream().noneMatch((e) -> e.getStatus().equals(Status.BUSY));
			System.out.println(bl2);
	}
	
	@Test
	public void test_find(){
		Optional<Employee> op = emps.stream()
			                        .sorted((e1, e2) -> Double.compare(e1.getSalary(), e2.getSalary()))
			                        .findFirst();
		System.out.println(op.get());
		System.out.println("--------------------------------");
		
		Optional<Employee> op2 = emps.parallelStream()
			                         .filter((e) -> e.getStatus().equals(Status.FREE))
			                         .findAny();
		System.out.println(op2.get());
	}
	
	@Test
	public void test_compare(){
		long count = emps.stream().filter((e) -> e.getStatus().equals(Status.FREE)).count();
		System.out.println(count);
		
		Optional<Double> op = emps.stream().map(Employee::getSalary).max(Double::compare);
		System.out.println(op.get());
		
		Optional<Employee> op2 = emps.stream().min((e1, e2) -> Double.compare(e1.getSalary(), e2.getSalary()));
		System.out.println(op2.get());
	}
	
	//注意：流进行了终止操作后，不能再次使用
	@Test
	public void test_terminal(){
		Stream<Employee> stream = emps.stream().filter((e) -> e.getStatus().equals(Status.FREE));
		stream.count();
		stream.map(Employee::getSalary).max(Double::compare);
	}
	
	/**
	 * 2.归约 —— 可以将流中元素反复结合起来，得到一个值。
	 * reduce(T identity, BinaryOperator) 
	 * reduce(BinaryOperator) 
	 */
	@Test
	public void test_reduce(){
		List<Integer> list = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
	
//		int sum = 0;
//		for(int i=0;i<10;i++){
//			sum+=i;
//		}
		
		Integer sum = list.stream().reduce(0, (x, y) -> x + y);
		System.out.println(sum);
		System.out.println("----------------------------------------");
		
		Optional<Double> op = emps.stream()
			                      .map(Employee::getSalary)
			                      .reduce(Double::sum);
		System.out.println(op.get());
	}
	
	//需求：搜索名字中 “六” 出现的次数
	@Test
	public void test_map_reduce(){
		Optional<Integer> sum = emps.stream()
									.map(Employee::getName)
									.flatMap(TestStream2_MiddleOperate::filterCharacter)
									.map((ch) -> {
										if(ch.equals('六'))
											return 1;
										else 
											return 0;
									})
									.reduce(Integer::sum);
		System.out.println(sum.get());
	}
	
	/**
	 * 3.收集 
	 * collect -- 将流转换为其他形式。接收一个 Collector接口的实现，用于给Stream中元素做汇总的方法
	 */
	@Test
	public void test_collect_to(){
		emps.stream()
		    .map(Employee::getName)
		    .collect(Collectors.toList())
		    .forEach(System.out::println);
		System.out.println("----------------------------------");
		
		emps.stream()
			.map(Employee::getName)
			.collect(Collectors.toSet())
			.forEach(System.out::println);
		System.out.println("----------------------------------");
		
		emps.stream()
			.map(Employee::getName)
			.collect(Collectors.toCollection(HashSet::new))
			.forEach(System.out::println);
	}
	
	// 计算
	@Test
	public void test_collect_compute(){
		Optional<Double> max = emps.stream()
			.map(Employee::getSalary)
			.collect(Collectors.maxBy(Double::compare));
		System.out.println(max.get());
		
		Optional<Employee> op = emps.stream()
			.collect(Collectors.minBy((e1, e2) -> Double.compare(e1.getSalary(), e2.getSalary())));		
		System.out.println(op.get());
		
		Double sum = emps.stream()
			.collect(Collectors.summingDouble(Employee::getSalary));
		System.out.println(sum);
		
		Double avg = emps.stream()
			.collect(Collectors.averagingDouble(Employee::getSalary));
		System.out.println(avg);
		
		Long count = emps.stream().collect(Collectors.counting());
		System.out.println(count);
		System.out.println("--------------------------------------------");
		
		DoubleSummaryStatistics dss = emps.stream()
			.collect(Collectors.summarizingDouble(Employee::getSalary));
		System.out.println(dss.getSum());
		System.out.println(dss.getAverage());
		System.out.println(dss.getMax());
	}
	
	//分组
	@Test
	public void test_group(){
		Map<Status, List<Employee>> map = emps.stream().collect(Collectors.groupingBy(Employee::getStatus));
		System.out.println(map);
	}
	
	//多级分组
	@Test
	public void test_mutil_group() {
		Map<Status, Map<String, List<Employee>>> map = emps.stream().collect(Collectors.groupingBy(Employee::getStatus, Collectors.groupingBy((Employee e) -> {
			if (e.getAge() >= 60)
				return "老年";
			else if (e.getAge() >= 35)
				return "中年";
			else
				return "成年";
		})));
		System.out.println(map);
	}
	
	//分区
	@Test
	public void test_partion(){
		Map<Boolean, List<Employee>> map = emps.stream().collect(Collectors.partitioningBy((e) -> e.getSalary() >= 5000));
		System.out.println(map);
	}
	
	//拼接
	@Test
	public void test_join(){
		String str = emps.stream().map(Employee::getName).collect(Collectors.joining("," , "----", "----"));
		System.out.println(str);
	}
	
	@Test
	public void test(){
		Optional<Double> sum = emps.stream()
			.map(Employee::getSalary)
			.collect(Collectors.reducing(Double::sum));
		System.out.println(sum.get());
	}
}
