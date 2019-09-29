package com.pepper.web.config;

import com.pepper.web.security.AuthFilter;
import com.pepper.web.security.MessyCodeFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.CharacterEncodingFilter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.servlet.Filter;

/**
 * @Author: pei.nie
 * @Date:2019/9/24
 * @Description:
 */
@Configuration
public class BeansConfig {

    @Bean
    @Order(1)
    public Filter characterEncodingFilter() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);
        return filter;
    }

    @Bean
    @Order(2)
    public Filter authFilter() {
        return new AuthFilter();
    }


    @Bean
    @Order(3)
    public Filter messyCodeFilter() {
        return new MessyCodeFilter();
    }

    @Value("${spring.redis.host}")
    private String redisHost;
    @Value("${spring.redis.port}")
    private Integer redisPort;
    @Value("${spring.redis.password}")
    private String redisPassword;
    @Value("${spring.redis.pool.max-idle}")
    private int redisPoolMaxIdle;
    @Value("${spring.redis.pool.min-idle}")
    private int redisPoolMinIdle;
    @Value("${spring.redis.pool.max-wait}")
    private long redisPoolMaxWait;

    @Bean
    public Jedis jedis() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(redisPoolMaxIdle);
        config.setMinIdle(redisPoolMinIdle);
        config.setMaxWaitMillis(redisPoolMaxWait);
        config.setTestOnBorrow(false);
        config.setTestOnReturn(true);
        JedisPool jedisPool = new JedisPool(config,redisHost,redisPort,2000);
        Jedis jedis = jedisPool.getResource();
        jedis.auth(redisPassword);
        return jedis;
    }
}
