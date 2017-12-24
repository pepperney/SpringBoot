package com.pepper.boot.service;

import java.util.List;

import com.pepper.boot.model.entity.UserInfo;



public interface UserInfoService {
	
	void addUser(UserInfo user);

	void delUser(int userId);

	void updateUser(UserInfo user);
	
	UserInfo getUser(int userId);
	
	List<UserInfo> getAllUsers();
	
	UserInfo getUserDetail(int userId);
	
	UserInfo getByToken(String token);

	
}
