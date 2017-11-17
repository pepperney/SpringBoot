package com.pepper.boot.security;

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import com.pepper.boot.config.SecurityProperties;
import com.pepper.boot.consts.SystemCode;
import com.pepper.boot.util.IPUtil;

public class TimestampChecker extends AbstractChecker {

	public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

	private static final Logger log = LoggerFactory.getLogger(TimestampChecker.class);

	private Environment env;

	public TimestampChecker(SecurityProperties securityProperties, Environment env) {
		super(securityProperties);
		this.env = env;
	}

	@Override
	public SystemCode check(HttpServletRequest request, HttpServletResponse response) {

		boolean skipCheck = true;
		
		if (skipCheck) {
			return SystemCode.SYSTEM_SUCCESS;
		}

		if (env.getActiveProfiles() != null && env.getActiveProfiles().length == 1 && "local".equals(env.getActiveProfiles()[0])) {
			return SystemCode.SYSTEM_SUCCESS;
		}

		String timestamp = getTimestamp(request);
		if (StringUtils.isBlank(timestamp)) {
			log.warn("xxx-----> The request {} from {}, timestamp is null.", getRequestPath(request), IPUtil.getClientIP(request));
			return SystemCode.CHECK_TIMESTAMP_FAIL;
		}

		try {
			long reqTime = DateUtils.parseDate(timestamp, DATE_FORMAT_PATTERN).getTime();
			// 检查timestamp 与系统时间是否相差在合理时间内，这里设为10分钟。
			boolean flag = Math.abs(System.currentTimeMillis() - reqTime) < getTimestampMilliseconds();
			if (flag) {
				return SystemCode.SYSTEM_SUCCESS;
			} else {
				log.warn("xxx-----> The request {} from {}, timestamp {} check faild.", getRequestPath(request), IPUtil.getClientIP(request), timestamp);
				return SystemCode.CHECK_TIMESTAMP_FAIL;
			}
		} catch (ParseException pe) {
			log.warn("xxx-----> The request {} from {}, timestamp {} parse faild.", getRequestPath(request), IPUtil.getClientIP(request), timestamp);

			return SystemCode.CHECK_TIMESTAMP_FAIL;
		}
	}
}
