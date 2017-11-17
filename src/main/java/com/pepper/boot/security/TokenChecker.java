package com.pepper.boot.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pepper.boot.config.SecurityProperties;
import com.pepper.boot.consts.Const;
import com.pepper.boot.consts.SystemCode;
import com.pepper.boot.model.entity.UserInfo;
import com.pepper.boot.service.RedisService;
import com.pepper.boot.service.UserInfoService;
import com.pepper.boot.util.IPUtil;
import com.pepper.boot.util.JsonUtil;

public class TokenChecker extends AbstractChecker {
	
	private static final Logger log = LoggerFactory.getLogger(TokenChecker.class);

	private UserInfoService userInfoService;

	private RedisService redisService;

	public TokenChecker(SecurityProperties securityProperties, UserInfoService userInfoService, RedisService redisService) {
		super(securityProperties);
		this.userInfoService = userInfoService;
		this.redisService = redisService;
	}

	@Override
	public SystemCode check(HttpServletRequest request, HttpServletResponse response) {
		String token = getxToken(request);
		if (StringUtils.isEmpty(token)) {
			log.info("xxx-----> The request {} from {},token is null.", getRequestPath(request), IPUtil.getClientIP(request));
			return SystemCode.CHECK_TOKEN_EXPIRE;
		}
		response.addHeader(securityProperties.getxToken(), token);
		UserInfo userInfo = null;
		String value = redisService.get(Const.TOKEN_KEY + token);
		if (StringUtils.isNotEmpty(value)) {
			userInfo = JsonUtil.jsonToObject(value, UserInfo.class);
		} else {
			log.info("xxx-----> The request {} from {},token is {}.", getRequestPath(request), IPUtil.getClientIP(request), token);
			userInfo = userInfoService.getByToken(token);
			this.redisService.setex(Const.TOKEN_KEY + token, Const.EXPIRE_TIME_TOKEN, JsonUtil.toJson(userInfo));
		}

		if (userInfo == null) {
			log.info("xxx-----> The request {} from {},token {} not found.", getRequestPath(request), IPUtil.getClientIP(request), token);
			return SystemCode.CHECK_TOKEN_EXPIRE;
		}
		
		request.setAttribute(USERDETAILS_KEY, userInfo);
		return SystemCode.SYSTEM_SUCCESS;
	}
}
