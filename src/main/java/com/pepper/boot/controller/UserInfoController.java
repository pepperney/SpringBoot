package com.pepper.boot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pepper.boot.amqp.Producer;
import com.pepper.boot.model.CommonResp;
import com.pepper.boot.model.entity.UserInfo;
import com.pepper.boot.service.UserInfoService;

@RestController
@RequestMapping("/user")
public class UserInfoController {
	
	@Autowired
	private UserInfoService  userInfoService;
	
	@Autowired
	private Producer producer;

	
 	
	@RequestMapping("/v1/userInfo")
	public ResponseEntity<Object> getUserInfo(@RequestParam("userId") int userId){
		
		UserInfo data = userInfoService.getUserDetail(userId);
		return CommonResp.returnOKResult(data);
		
	}
	
	
	/*@RequestMapping("/v1/login")
	public ResponseEntity<Object> login(@RequestBody UserInfo userVo){
		
		UserInfo data = userInfoService.getUserDetail(userId);
		return CommonResp.returnOKResult(data);
		
	}*/
	
	
	@GetMapping("/v1/send")
	public ResponseEntity<Object> send( ) {
		producer.send();
		return CommonResp.returnOK();
	}

}
