package com.pepper.web.controller;

import java.util.HashMap;
import java.util.Map;

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

@RestController
@RequestMapping("/user")
public class UserInfoController {
	
	@Autowired
	private UserInfoService  userInfoService;
	
	@Autowired
	private Producer producer;

	
	
	/**
	 * test for redis and mysql 
	 * @param userId
	 * @return
	 */
	@RequestMapping("/v1/userInfo")
	public ResponseEntity<Object> getUserInfo(@RequestParam("userId") int userId){
		UserInfo data = userInfoService.getUserDetail(userId);
		return CommonResp.returnOKResult(data);
		
	}
		
	
	/**
	 * test for rabbitmq topic exchange and queue
	 * @return
	 */
	@GetMapping("/v1/send")
	public ResponseEntity<Object> send( ) {
		producer.send();
		return CommonResp.returnOK();
	}
	
	
	/**
	 * test for restTemplate
	 * @return
	 */
	@GetMapping("/test")
	public String  get() {
		String url = "http://localhost:9090/mine";
		Map<String,String> params = new HashMap<>();
		params.put("address", "12345");	
		String result = HttpUtil.get(url, params, null);
		return result;
	}

}
