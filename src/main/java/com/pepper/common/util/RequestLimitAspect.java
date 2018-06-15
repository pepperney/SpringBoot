package com.pepper.common.util;

import javax.servlet.http.HttpServletRequest;

import com.pepper.web.helper.RedisHelper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.pepper.common.annotation.RequestLimit;
import com.pepper.common.consts.Code;
import com.pepper.common.exception.CustomException;

@Aspect
@Component
public class RequestLimitAspect {

	private static final Logger logger = LoggerFactory.getLogger(RequestLimitAspect.class);

	@Autowired
	private RedisHelper redisHelper;
	
	@Pointcut("@annotation(com.pepper.common.annotation.RequestLimit)")  
    public void requestPoint(){  
    } 

	@Before("requestPoint() && @annotation(limit)")
	public void requestLimit(JoinPoint joinPoint,RequestLimit limit) throws Throwable {

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String ip = IPUtil.getClientIP(request);
		String url = request.getRequestURL().toString();
		String key = "req_limit_".concat(url).concat(ip);
		long count = redisHelper.increment(key, 1L);
		if (count > 0) {
			redisHelper.expire(key, limit.time());
		}
		if (count > limit.count()) {
			logger.info("用户IP[" + ip + "]访问地址[" + url + "]超过了限定的次数[" + limit.count() + "]");
			throw new CustomException(Code.SYSTEM_ERROR, "您的操作太过频繁，请稍后再试");
		}

	}

}
