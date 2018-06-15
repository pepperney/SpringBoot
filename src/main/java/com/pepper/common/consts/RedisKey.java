package com.pepper.common.consts;

public enum RedisKey {



    TOKEN       (RedisKey.PREFIX + "token_", 1 * 24 * 60 * 60),

    USER_ID     (RedisKey.PREFIX + "userId_", 5 * 60),

    ;









    /** global redis key prefix */
    public static final String PREFIX = "rd_";

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
