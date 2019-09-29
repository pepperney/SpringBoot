package com.pepper.web.serviceImpl;

import com.pepper.web.helper.RedisHelper;
import com.pepper.web.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: pei.nie
 * @Date:2019/9/20
 * @Description:
 */
@Component
public class RedisCacheServiceImpl implements CacheService {

    @Autowired
    private RedisHelper redisHelper;

    @Override
    public String getCache(String key) {
        return redisHelper.get(key);
    }

    @Override
    public void setCache(String key, int seconds, String value) {
        redisHelper.setex(key,seconds,value);
    }
}
