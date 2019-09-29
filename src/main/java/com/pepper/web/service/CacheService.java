package com.pepper.web.service;

public interface CacheService {
    /**
     * 添加缓存
     *
     * @param key
     * @return
     */
    String getCache(String key);

    /**
     * 获取缓存
     * @param key
     * @param expireTime 过期时间，单位s
     * @param value
     */
    void setCache(String key, int expireTime, String value);

}