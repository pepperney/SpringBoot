package com.pepper.common.consts;

public enum RedisKey {

	TOKEN_KEY	(RedisKey.PREFIX + "token", 30 * 60 * 20);

	public static final String PREFIX = "system_";

	private String key;

	private Integer expireTime;

	RedisKey(String key, Integer expireTime) {
		this.key = key;
		this.expireTime = expireTime;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Integer getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Integer expireTime) {
		this.expireTime = expireTime;
	}

}
