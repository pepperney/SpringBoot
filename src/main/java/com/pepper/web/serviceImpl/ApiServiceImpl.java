package com.pepper.web.serviceImpl;

import com.pepper.common.util.JsonUtil;
import com.pepper.common.util.ThreadPoolUtil;
import com.pepper.web.model.entity.RetryTask;
import com.pepper.web.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ApiServiceImpl implements ApiService {


    @Autowired
    private MyRetryServiceImpl myRetryService;

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
}
