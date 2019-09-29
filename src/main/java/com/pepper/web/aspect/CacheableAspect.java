package com.pepper.web.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pepper.common.annotation.Cacheable;
import com.pepper.common.util.JsonUtil;
import com.pepper.web.service.CacheService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Aspect
@Component
public class CacheableAspect extends AbstractCacheAspect {

    private static final Logger log = LoggerFactory.getLogger(CacheableAspect.class);

    @Autowired
    private CacheService cacheService;

    @Pointcut("@annotation(com.pepper.common.annotation.Cacheable)")
    public void methodCacheableAnnotationPointcut() {

    }

    @Around("methodCacheableAnnotationPointcut()")
    public Object invokeCacheableMethod(ProceedingJoinPoint joinPoint) throws Throwable {

        String className = joinPoint.getTarget().getClass().getName() + ".";
        Method method = resolveMethod(joinPoint);
        String params = JSON.toJSONString(joinPoint.getArgs());
        Cacheable annotation = method.getAnnotation(Cacheable.class);

        if (annotation == null) {
            throw new IllegalStateException("Wrong state for Cacheable here");
        }
        // 方法名作为key的前缀，方法入参的hashcode作为后缀组成缓存的key值，来实现不同请求参数缓存不同的结果
        String cacheKey = className + method.getName() + "@" + params.hashCode();
        int cacheTimes = annotation.time();
        Type returnType = method.getGenericReturnType();
        try {
            String cacheValue = cacheService.getCache(cacheKey);
            if (StringUtils.isNotEmpty(cacheValue)) {
                log.debug("[{}] response has been cached", className + method.getName());
                Object object = JSONObject.parse(cacheValue);
                if (object instanceof JSONObject) {
                    return JSON.parseObject(cacheValue, returnType);
                } else if (object instanceof JSONArray) {
                    Type[] returnArgTypes = ParameterizedType.class.cast(returnType).getActualTypeArguments();
                    return JSONObject.parseArray(cacheValue, Class.forName(returnArgTypes[0].getTypeName()));
                } else {
                    return object;
                }
            }
            Object result = joinPoint.proceed();
            log.debug("set method cache,key is [{}],value is {}", cacheKey, JSON.toJSONString(result));
            cacheService.setCache(cacheKey, cacheTimes, JSON.toJSONString(result));
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

}
