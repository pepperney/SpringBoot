package com.pepper.learn.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: pei.nie
 * @Date:2019/1/16
 * @Description:
 */
public class AtomicTest {

    static int number = 0;
    static AtomicInteger count = new AtomicInteger(0);

    /**
     * 非原子类多线程i++
     * @throws Exception
     */
    public static void testInteger() throws Exception {
        CountDownLatch latch = new CountDownLatch(100);
        for (int i = 0; i < 100; i++) {
            Runnable runnable = () -> {
                for (int j = 0; j < 100; j++) {
                    number++;
                }
                latch.countDown();
            };
            new Thread(runnable).start();
        }
        latch.await();
        System.out.println(number);
    }

    /**
     * 原子类多线程i++
     *
     * @throws Exception
     */
    public static void testAomicInteger() throws Exception {
        CountDownLatch latch = new CountDownLatch(100);
        for (int i = 0; i < 100; i++) {
            Runnable runnable = () -> {
                for (int j = 0; j < 100; j++) {
                    count.incrementAndGet();
                }
                latch.countDown();
            };
            new Thread(runnable).start();
        }
        latch.await();
        System.out.println(count);
    }

    public static void main(String[] args) throws Exception {
//        testInteger();
        System.out.println("---------------");
        testAomicInteger();
    }
}
