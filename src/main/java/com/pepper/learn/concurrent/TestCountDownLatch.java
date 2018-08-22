package com.pepper.learn.concurrent;

import java.util.concurrent.CountDownLatch;

/**
 * @Auther: pei.nie
 * @Date:2018/8/4
 * @Description:演示闭锁的使用
 */
public class TestCountDownLatch {

    public long tasks(int nThreads, final Runnable task) throws InterruptedException {
        final CountDownLatch startLatch = new CountDownLatch(1);//计数器初始化为1，控制主线程的状态
        final CountDownLatch endLatch = new CountDownLatch(50);//计数器初始化为工作线程的数量50，控制工作线程的状态
        for (int i = 0; i < nThreads; i++) {
            Thread t = new Thread() {
                public void run() {
                    try {
                        startLatch.await();//每个线程都必须先等待startLatch打开，确保所有线程都准备好才开始工作
                        try {
                            task.run();
                        } finally {
                            endLatch.countDown();//每个线程的最后一个工作就是为endLatch减一
                        }
                    } catch (InterruptedException e) {
                    }
                }
            };
            t.start();
        }
        long start = System.currentTimeMillis();
        startLatch.countDown();//startLatch减一之后，不再阻塞，线程开始执行
        endLatch.await();//endLatch阻塞到所有线程执行完任务
        long end = System.currentTimeMillis();
        return end - start;
    }

}
