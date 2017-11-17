package com.pepper.boot.service;

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
}
