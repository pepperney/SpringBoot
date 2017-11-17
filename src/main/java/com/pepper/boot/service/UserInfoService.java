package com.pepper.boot.service;

import com.pepper.boot.model.entity.UserInfo;



public interface UserInfoService {
	
	UserInfo getUserDetail(int userId);

	UserInfo getByToken(String token);
}
