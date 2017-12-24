package com.pepper.boot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pepper.boot.amqp.Producer;
import com.pepper.boot.model.CommonResp;
import com.pepper.boot.model.entity.UserInfo;
import com.pepper.boot.service.UserInfoService;

@Controller
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

}
