package com.pepper.web.serviceImpl;

import com.pepper.common.annotation.Cacheable;
import com.pepper.common.util.JsonUtil;
import com.pepper.common.util.ThreadPoolUtil;
import com.pepper.web.helper.RedisHelper;
import com.pepper.web.model.entity.RetryTask;
import com.pepper.web.service.ApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ApiServiceImpl implements ApiService {

    private Logger logger = LoggerFactory.getLogger(ApiServiceImpl.class);

    @Autowired
    private MyRetryServiceImpl myRetryService;

    @Autowired
    private RedisHelper redisHelper;

    private Integer count = 0;

    @Override
    public void doRetry() {
        Map<String, String> content = new HashMap<>();
        content.put("key", "value");
        RetryTask retryTask = new RetryTask();
        retryTask.setType("1");
        retryTask.setBizNo("123456789");
        retryTask.setContent(JsonUtil.toJson(content));
        retryTask.setStatus(RetryTask.STATUS_DEFAULT);
        retryTask.setUrl("https://wwww.baiduyixialuanqiba.com");
        myRetryService.addRetryTask(retryTask);
        ThreadPoolUtil.execute(new Runnable() {
            @Override
            public void run() {
                myRetryService.retry(retryTask);
            }
        });

    }

    @Async
    @Override
    public void logValue(Object param) {
        logger.info("ApiService#logValue-->param={}", JsonUtil.toJson(param));
    }

    @Cacheable
    @Override
    public String cacheValue(String args) {
        logger.info("begin excute method ApiService#cacheValue({})",args);
        return args;
    }

    @Override
    public Integer addCount(String key) {
        try {
            logger.info("{}进入,count={}", Thread.currentThread().getName(), count);
            if (redisHelper.lock(key, key, 1000)) {
                count++;
                Thread.sleep(1000);
                logger.info("{}修改成功,count={}", Thread.currentThread().getName(), count);
                redisHelper.unLock(key, key);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return count;
    }


}
