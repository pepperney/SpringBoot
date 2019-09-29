package com.pepper.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

/**
 * @Author: pei.nie
 * @Date:2019/9/23
 * @Description:
 */
@Configuration
@EnableAsync
public class AsyncExecutorConfig implements AsyncConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(AsyncExecutorConfig.class);

    @Value("${thread.maxSize:20}")
    private Integer maxSize;

    @Value("${thread.coreSize:20}")
    private Integer coreSize;

    @Value("${thread.queueSize:200}")
    private Integer queueSize;

    /**
     * 替换默认线程池
     *
     * @return
     */
    @Override
    @Bean("myExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(maxSize);
        executor.setCorePoolSize(coreSize);
        executor.setQueueCapacity(queueSize);
        executor.setThreadNamePrefix("SpringAsyncExecutor-");
        executor.setAllowCoreThreadTimeOut(true);
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new MyAsyncUncaughtExceptionHandler();
    }


    public static class MyAsyncUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {
        @Override
        public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
            logger.error("Exception message={}, methodName={}", throwable.getMessage(), method.getName(), throwable);
            for (Object param : objects) {
                logger.error("Parameter value - " + param);
            }
        }
    }

}