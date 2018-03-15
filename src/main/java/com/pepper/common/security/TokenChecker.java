package com.pepper.common.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pepper.common.consts.Const;
import com.pepper.common.consts.SystemCode;
import com.pepper.common.util.IPUtil;
import com.pepper.common.util.JsonUtil;
import com.pepper.common.util.RedisUtil;
import com.pepper.config.SecurityProperties;
import com.pepper.web.model.entity.UserInfo;
import com.pepper.web.service.UserInfoService;

public class TokenChecker extends AbstractChecker {
	
	private static final Logger log = LoggerFactory.getLogger(TokenChecker.class);

	private UserInfoService userInfoService;

	private RedisUtil redisUtil;

	public TokenChecker(SecurityProperties securityProperties, UserInfoService userInfoService, RedisUtil redisUtil) {
		super(securityProperties);
		this.userInfoService = userInfoService;
		this.redisUtil = redisUtil;
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
		String value = redisUtil.get(Const.TOKEN_KEY + token);
		if (StringUtils.isNotEmpty(value)) {
			userInfo = JsonUtil.jsonToObject(value, UserInfo.class);
		} else {
			log.info("xxx-----> The request {} from {},token is {}.", getRequestPath(request), IPUtil.getClientIP(request), token);
			userInfo = userInfoService.getByToken(token);
			this.redisUtil.setex(Const.TOKEN_KEY + token, Const.EXPIRE_TIME_TOKEN, JsonUtil.toJson(userInfo));
		}

		if (userInfo == null) {
			log.info("xxx-----> The request {} from {},token {} not found.", getRequestPath(request), IPUtil.getClientIP(request), token);
			return SystemCode.CHECK_TOKEN_EXPIRE;
		}
		
		request.setAttribute(USERDETAILS_KEY, userInfo);
		return SystemCode.SYSTEM_SUCCESS;
	}
}
