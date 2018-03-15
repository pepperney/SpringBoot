package com.pepper.common.util;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AspectUtil {
	private static final Logger log = LoggerFactory.getLogger(AspectUtil.class);

	@SuppressWarnings("rawtypes")
	@Around("execution(* com.pepper.boot.controller.*Controller.*(..))")
	public Object process(ProceedingJoinPoint joinPoint) throws Throwable {
		Object respData;
		long startTime = System.currentTimeMillis();
		String className = joinPoint.getTarget().getClass().getSimpleName(); // 获取类名(这里只切面了Controller类)
		String methodName = joinPoint.getSignature().getName(); // 获取方法名
		String methodInfo = className + "." + methodName; // 组织类名.方法名
		//打印Controller入参
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		log.info(methodInfo + "()-->" + request.getRequestURI() + "被调用, 客户端IP=" + IPUtil.getClientIP(request) + ", 入参为" + JsonUtil.toJson(request.getParameterMap()));
		//执行Controller的方法 
		respData = joinPoint.proceed();
		long endTime = System.currentTimeMillis();
		String returnInfo;
		if (null != respData && respData.getClass().isAssignableFrom(ResponseEntity.class)) {
			returnInfo = JsonUtil.toJson(((ResponseEntity) respData).getBody());
		} else {
			returnInfo = JsonUtil.toJson((respData));
		}
		//打印Controller出参
		log.info(methodInfo + "()-->" + request.getRequestURI() + "被调用, 出参为" + returnInfo + ", 耗时[" + (endTime - startTime) + "]ms");
		log.info("---------------------------------------------------------------------------------------------");
		return respData;
	}

}
