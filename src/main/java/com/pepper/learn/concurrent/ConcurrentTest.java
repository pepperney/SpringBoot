package com.pepper.learn.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

/**
 * @Author: pei.nie
 * @Date:2019/1/15
 * @Description:
 */
public class ConcurrentTest {

    /**
     * 闭锁
     *
     * @param nThreads
     * @throws InterruptedException
     */
    public static void testCountDownLatch(int nThreads) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(nThreads);
        for (int i = 0; i < nThreads; i++) {
            Thread t = new Thread("学生" + (i + 1)) {
                public void run() {
                    try {
                        System.out.println(Thread.currentThread().getName() + "收拾东西中...");
                        Thread.sleep(1000);
                        System.out.println(Thread.currentThread().getName() + "离开教室...");
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        latch.countDown();//每个线程的最后一个工作就是为latch减一
                    }
                }
            };
            t.start();
        }
        latch.await();//此处还有替换成带超时时间的await()方法
        System.out.println("所有学生都已离开,开始锁门");
    }


    /**
     * 栅栏
     *
     * @param nThreads
     * @throws Exception
     */
    public static void testCyclicBarrier(int nThreads) throws Exception {
        //当拦截线程数达到4时，便优先执行barrierAction，然后再执行被拦截的线程。
        final CyclicBarrier barrier = new CyclicBarrier(nThreads);
        System.out.println("游戏组队中......");
        for (int i = 0; i < nThreads; i++) {
            Thread t = new Thread("玩家-" + (i + 1)) {
                public void run() {
                    try {
                        barrier.await();//拦截线程
                        System.out.println("等待[" + Thread.currentThread().getName() + "]进入游戏");
                        Thread.sleep(1000);
                        System.out.println("[" + Thread.currentThread().getName() + "]成功进入游戏");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            t.start();
        }
        Thread.sleep(2000);
        System.out.println("游戏开始！");
    }

    /**
     * 信号量
     *
     * @param nThreads
     */
    public static void testSemaphore(int nThreads) {
        Semaphore semaphore = new Semaphore(4);
        for (int i = 0; i < nThreads; i++) {
            Thread t = new Thread("座位号-" + (i + 1)) {
                public void run() {
                    try {
                        semaphore.acquire();
                        System.out.println(Thread.currentThread().getName() + "当前就餐中...");
                        Thread.sleep(2000);
                        System.out.println(Thread.currentThread().getName() + "用餐结束");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        semaphore.release();
                    }
                }
            };
            t.start();
        }

    }


    public static void main(String[] args) throws Exception {
//        testCountDownLatch(5);
//        System.out.println("------------------------\n");
//        testCyclicBarrier(5);
//        System.out.println("------------------------\n");
        testSemaphore(5);
    }


}
