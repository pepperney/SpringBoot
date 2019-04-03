package com.pepper.common.aspectj;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pepper.common.annotation.MethodCacheable;
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
public class MethodCacheableAspect extends AbstractCacheAspectSupport{

    private static final Logger log = LoggerFactory.getLogger(MethodCacheableAspect.class);

    @Autowired
    private CacheService cacheService;

    @Pointcut("@annotation(com.pepper.common.annotation.MethodCacheable)")
    public void methodCacheableAnnotationPointcut(){

    }

    @Around("methodCacheableAnnotationPointcut()")
    public Object invokeCacheableMethod(ProceedingJoinPoint joinPoint) throws Throwable{

        Method method = resolveMethod(joinPoint);

        MethodCacheable annotation = method.getAnnotation(MethodCacheable.class);

        if(annotation == null){
            throw new IllegalStateException("Wrong state for MethodCacheable here");
        }

        String cacheKey = annotation.key();
        int cacheTimes = annotation.time();

        Type returnType = method.getGenericReturnType();
        try {
            String cacheValue = cacheService.getCache(cacheKey);
            log.debug("the method {},key is {},value is {}",method.getName(),cacheKey,cacheValue);
            if(StringUtils.isNotEmpty(cacheValue)){
                Object object = JSONObject.parse(cacheValue);
                if(object instanceof JSONObject){
                    return JSON.parseObject(cacheValue,returnType);
                }else if(object instanceof JSONArray){
                    Type[] returnArgTypes  = ParameterizedType.class.cast(returnType).getActualTypeArguments();
                    return JSONObject.parseArray(cacheValue, Class.forName(returnArgTypes[0].getTypeName()));
                }
            }
            Object result = joinPoint.proceed();
            log.debug("set method {} cache,key is {},value is {}",method.getName(),cacheKey,JSON.toJSONString(result));
            cacheService.setCache(cacheKey,cacheTimes,JSON.toJSONString(result));
            return result;
        } catch (Throwable ex){
            throw ex;
        }
    }

}
