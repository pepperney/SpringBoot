package com.pepper.common.aspectj;


public interface CacheService {

    String getCache(String key);

    void setCache(String key,int seconds,String value);
}
