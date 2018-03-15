package com.pepper.boot.service;

import redis.clients.jedis.Jedis;

public interface RedisService {
	
	
	/**
	 * get value by key
	 * @param key
	 * @return
	 */
	public String get(String key);
	/**
	 * set key and value
	 * @param key
	 * @param value
	 */
	public void set(String key,String value);
	/**
	 * set with an expire time
	 * @param key
	 * @param expireTime
	 * @param value
	 */
	public void setex(String key,long expireTime,String value);
	/**
	 * set when the key isn't exist
	 * @param key
	 * @param expireTime
	 * @param value
	 */
	public void setnx(String key,long expireTime,String value);
	/**
	 * Returns the remaining time to live of the key that has a timeout.  
	 * @param key
	 * @return
	 */
	public long ttl(String key);
	/**
	 * del the key
	 * @param key
	 */
	public void del(String key);
	/**
	 * add lock
	 * @see http://mp.weixin.qq.com/s/qJK61ew0kCExvXrqb7-RSg
	 * @param jedis Redis客户端
     * @param key 锁
     * @param requestId 请求标识
     * @param expireTime 超期时间
     * @return 是否获取成功
	 */
	public boolean lock(Jedis jedis,String key,String requestId,int expireTime);
	/**
	 * release lock
     * @param jedis Redis客户端
     * @param key 锁
     * @param requestId 请求标识
     * @return 是否释放成功
	 */
	public boolean unLock(Jedis jedis,String key,String requestId);
}
