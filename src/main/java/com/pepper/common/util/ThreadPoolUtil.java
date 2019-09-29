package com.pepper.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class ThreadPoolUtil {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolUtil.class);

    private final int corePoolSize = 20;
    private final int maxPoolSize = 20;
    private final int queueCapacity = 200;
    private final int keepAliveTime = 60;

    private static ThreadPoolUtil instance = new ThreadPoolUtil();
    private String threadName = "CustomThread-";
    private static ThreadPoolExecutor pool;
    private final BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(queueCapacity);
    private final RejectedExecutionHandler handler = (r, executor) -> {
        try {
            executor.getQueue().put(r);
        } catch (InterruptedException e) {
            logger.error("添加任务失败", e);
        }
    };


    private ThreadPoolUtil() {
        this.pool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue, handler);
        pool.setThreadFactory(new ThreadFactory() {

            private AtomicLong starts = new AtomicLong(0);

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(new ThreadGroup(threadName), r);
                t.setName(threadName + "-" + Long.toString(starts.incrementAndGet()));
                t.setDaemon(true);
                return t;
            }
        });
    }

    private static ThreadPoolUtil getInstance() {
        return instance;
    }

    public static <T> Future<T> execute(final Callable<T> runnable) {
        return getInstance().pool.submit(runnable);
    }

    public static Future<?> execute(final Runnable runnable) {
        return getInstance().pool.submit(runnable);
    }
}
