package com.pepper.boot.serviceImpl;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.pepper.boot.service.RedisService;

@Service
public class RedisServiceImpl implements RedisService {

	@Autowired
	StringRedisTemplate stringRedisTemplate;

	@Override
	public String get(String key) {
		if (StringUtils.isNotEmpty(key)) {
			return stringRedisTemplate.opsForValue().get(key);
		}
		return null;
	}

	@Override
	public void setex(String key, long expireTime, String value) {
		stringRedisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.SECONDS);
	}

	@Override
	public void set(String key, String value) {
		stringRedisTemplate.opsForValue().set(key, value);
	}

	@Override
	public long ttl(String key) {
		return stringRedisTemplate.getExpire(key) > 0 ? stringRedisTemplate.getExpire(key) : 0;
	}

	@Override
	public void del(String key) {
		stringRedisTemplate.delete(key);

	}

	@Override
	public void setnx(String key, long expireTime, String value) {
		stringRedisTemplate.opsForValue().setIfAbsent(key, value);

	}

}
