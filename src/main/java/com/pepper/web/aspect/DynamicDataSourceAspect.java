package com.pepper.web.aspect;

import com.pepper.web.datasource.DataSourceContextHolder;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class DynamicDataSourceAspect {

    @Pointcut("@annotation(com.pepper.web.aspect.Read)")
    public void readDataSourcePointcut(){

    }

    @Pointcut("@annotation(com.pepper.web.aspect.Write)")
    public void writeDataSourcePointcut(){

    }

    @Before("readDataSourcePointcut()")
    public void setReadDataSource(){
        DataSourceContextHolder.setRead();
    }

    @Before("writeDataSourcePointcut()")
    public void setWriteDataSource(){
        DataSourceContextHolder.setWrite();
    }

    @After("readDataSourcePointcut()||writeDataSourcePointcut()")
    public void clearDataSourceType(){
        DataSourceContextHolder.clearDataSourceType();
    }
}
