package com.pepper.boot.serviceImpl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pepper.boot.consts.Const;
import com.pepper.boot.dao.UserInfoMapper;
import com.pepper.boot.model.entity.UserInfo;
import com.pepper.boot.service.RedisService;
import com.pepper.boot.service.UserInfoService;
import com.pepper.boot.util.JsonUtil;

@Service
public class UserInfoServiceImpl implements UserInfoService {

	@Autowired
	UserInfoMapper userInfoMapper;

	@Autowired
	RedisService redisService;

	public static final String USER_KEY = Const.PREFIX + "userId_";

	@Override
	public UserInfo getUserDetail(int userId) {
		UserInfo data = null;
		String key = USER_KEY + userId;
		String value = redisService.get(key);
		if (StringUtils.isNotEmpty(value)) {
			data = JsonUtil.jsonToObject(value, UserInfo.class);
		} else {
			data = userInfoMapper.selectByPrimaryKey(userId);
			redisService.setex(key, Const.EXPIRE_TIME_USERID, JsonUtil.toJson(data));
		}
		return data;
	}

	@Override
	public UserInfo getByToken(String token) {
		// TODO Auto-generated method stub
		return null;
	}

}
