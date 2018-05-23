package com.pepper.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.pepper.common.util.JsonUtil;
import com.pepper.web.model.entity.RetryTask;
import com.pepper.web.service.ApiService;
import com.pepper.web.service.RetryService;
import com.pepper.web.serviceImpl.MyRetryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pepper.common.util.HttpUtil;
import com.pepper.config.amqp.Producer;
import com.pepper.web.model.CommonResp;
import com.pepper.web.model.entity.UserInfo;
import com.pepper.web.service.UserInfoService;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private ApiService apiService;

    @Autowired
    private Producer producer;


    /**
     * test for redis and mysql
     *
     * @param userId
     * @return
     */
    @RequestMapping("/userInfo")
    public ResponseEntity<Object> getUserInfo(@RequestParam("userId") int userId) {
        UserInfo data = userInfoService.getUserDetail(userId);
        return CommonResp.returnOKResult(data);

    }


    /**
     * test for rabbitmq topic exchange and queue
     *
     * @return
     */
    @GetMapping("/send")
    public ResponseEntity<Object> send() {
        producer.send();
        return CommonResp.returnOK();
    }


    /**
     * test for restTemplate
     *
     * @return
     */
    @GetMapping("/testGet")
    public String get() {
        String url = "http://localhost:9090/api/userInfo";
        Map<String, String> params = new HashMap<>();
        params.put("userId", "1");
        String result = HttpUtil.get(url, params, null);
        return result;
    }

    /**
     * test for retry
     *
     * @return
     */
    @GetMapping("/retry")
    public ResponseEntity<Object> retry() {
        apiService.doRetry();
        return CommonResp.returnOK();
    }


}
