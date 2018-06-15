package com.pepper.web.controller;

import java.util.HashMap;
import java.util.Map;

import com.pepper.common.consts.Const;
import com.pepper.web.model.MqMsg;
import com.pepper.web.service.ApiService;
import com.pepper.web.helper.RabbitSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pepper.common.util.HttpUtil;
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
    private RabbitSender rabbitSender;


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
        MqMsg mqMsg1 = new MqMsg(Const.MQ_EXCHANGE_TEST,"key.topic.test.1","hello,this is test message_1");
        rabbitSender.send(mqMsg1);
        MqMsg mqMsg2 = new MqMsg(Const.MQ_EXCHANGE_TEST,"key.topic.test.2","hello,this is test message_2");
        rabbitSender.send(mqMsg2);
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
