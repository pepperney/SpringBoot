package com.pepper.boot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pepper.boot.model.entity.UserInfo;
import com.pepper.boot.service.UserInfoService;

@Controller
public class IndexController {

	
	@Autowired
	private UserInfoService  userInfoService;
	
	
	@RequestMapping("/index")
	public String index() {
	  return "index";
	}
	
	@PostMapping("/user/addUser")
	public String addUser(@RequestBody UserInfo user){
		userInfoService.addUser(user);
		return "user/add";
	}
	
	@GetMapping("/user/delUser")
	public String delUser(@RequestParam("userId") int userId){
		userInfoService.delUser(userId);
		return "user/del";
	}
	
	@PostMapping("/user/updateUser")
	public String updateUser(@RequestBody UserInfo user){
		userInfoService.updateUser(user);
		return "user/update";
	}
	
	@GetMapping("/user/getUser")
	public String getUser(@RequestParam("userId") int userId,Model model){
		UserInfo user = userInfoService.getUser(userId);
		model.addAttribute("user",user);
		return "user/get";
		
	}
	
	@GetMapping("/user/userList")
	public String login(Model model){
		List<UserInfo> list = userInfoService.getAllUsers();
		model.addAttribute("userList",list);
		return "user/list";
	}
 	
	
	
	
	
	
	
	
	
}
