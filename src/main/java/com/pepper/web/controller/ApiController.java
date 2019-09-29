package com.pepper.web.controller;

import com.pepper.common.consts.Const;
import com.pepper.common.util.HttpUtil;
import com.pepper.web.helper.RabbitSender;
import com.pepper.web.model.CommonResp;
import com.pepper.web.model.MqMsg;
import com.pepper.web.model.entity.UserInfo;
import com.pepper.web.service.ApiService;
import com.pepper.web.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private ApiService apiService;

    @Autowired
    private RabbitSender rabbitSender;


    /**
     * test for redis and mysql
     *
     * @param userId
     * @return
     */
    @GetMapping("/userInfo")
    public ResponseEntity<Object> getUserInfo(@RequestParam("userId") int userId) {
        UserInfo data = userInfoService.getUserDetail(userId);
        return CommonResp.returnOKResult(data);
    }


    /**
     * test for swagger
     *
     * @param user
     * @return
     */
    @PostMapping("/saveUser")
    public ResponseEntity<Object> getUserInfo(@RequestBody UserInfo user) {
        userInfoService.saveUser(user);
        return CommonResp.returnOK();
    }


    /**
     * test for rabbitmq topic exchange and queue
     *
     * @return
     */
    @GetMapping("/send")
    public ResponseEntity<Object> send() {
        MqMsg mqMsg1 = new MqMsg(Const.MQ_EXCHANGE_TEST, "key.topic.test.1", "hello,this is test message_1");
        rabbitSender.send(mqMsg1);
        MqMsg mqMsg2 = new MqMsg(Const.MQ_EXCHANGE_TEST, "key.topic.test.2", "hello,this is test message_2");
        rabbitSender.send(mqMsg2);
        return CommonResp.returnOK();
    }


    /**
     * test for restTemplate
     *
     * @return
     */
    @GetMapping("/testRemote")
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
    @GetMapping("/testRetry")
    public ResponseEntity<Object> retry() {
        apiService.doRetry();
        return CommonResp.returnOK();
    }

    /**
     * test asyn
     * @return
     */
    @GetMapping("/testAsyn")
    public boolean testAsyn(){
        UserInfo userInfo = userInfoService.getUserDetail(1);
        apiService.logValue(userInfo);
        return true;
    }

    /**
     * test annotation CacheAble
     * @param args
     * @return
     */
    @GetMapping("/testCache")
    public String testCache(String args){
        return apiService.cacheValue(args);
    }

    /**
     * test redis lock
     * @param key
     * @throws InterruptedException
     */
    @GetMapping("/testLock")
    public void testLock(String key) throws InterruptedException {
        new Thread(() -> apiService.addCount(key), "thread-1").start();
        new Thread(() -> apiService.addCount(key), "thread-2").start();
    }

}
