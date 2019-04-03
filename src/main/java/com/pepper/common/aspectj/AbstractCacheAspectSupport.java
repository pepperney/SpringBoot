package com.pepper.common.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

public class AbstractCacheAspectSupport {


    protected Method resolveMethod(ProceedingJoinPoint joinPoint){
        //获取目标方法的定义
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        //获取目标类
        Class<?> targetClass = joinPoint.getTarget().getClass();

        //获取目标方法
        Method method = getDeclaredMethodFor(targetClass,signature.getName(),signature.getMethod().getParameterTypes());

        if (method == null) {
            throw new IllegalStateException("Cannot resolve target method: " + signature.getMethod().getName());
        }
        return method;
    }


    private Method getDeclaredMethodFor(Class<?> clazz, String name, Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                return getDeclaredMethodFor(superClass, name, parameterTypes);
            }
        }
        return null;
    }
}
