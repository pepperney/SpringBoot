package com.pepper.web.helper;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Component
public class RedisHelper {

	private static final Logger logger = LoggerFactory.getLogger(RedisHelper.class);

	private static final String LOCK_SUCCESS = "OK";
	private static final String SET_IF_NOT_EXIST = "NX";
	private static final String SET_WITH_EXPIRE_TIME = "PX";
	private static final Long RELEASE_SUCCESS = 1L;

	@Autowired
	StringRedisTemplate stringRedisTemplate;

	@Autowired
	private Jedis jedis;

	/**
	 * get value by key
	 *
	 * @param key
	 * @return
	 */
	public String get(String key) {
		if (StringUtils.isNotEmpty(key)) {
			return stringRedisTemplate.opsForValue().get(key);
		}
		return null;
	}

	/**
	 * set with an expire time
	 *
	 * @param key
	 * @param expireTime
	 * @param value
	 */
	public void setex(String key, long expireTime, String value) {
		stringRedisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.SECONDS);
	}

	/**
	 * set key and value
	 *
	 * @param key
	 * @param value
	 */
	public void set(String key, String value) {
		stringRedisTemplate.opsForValue().set(key, value);
	}

	/**
	 * Returns the remaining time to live of the key that has a timeout.
	 *
	 * @param key
	 * @return
	 */
	public long ttl(String key) {
		return stringRedisTemplate.getExpire(key) > 0 ? stringRedisTemplate.getExpire(key) : 0;
	}

	/**
	 * del the key
	 *
	 * @param key
	 */
	public void del(String key) {
		stringRedisTemplate.delete(key);

	}

	/**
	 * set when the key isn't exist
	 *
	 * @param key
	 * @param value
	 */
	public boolean setnx(String key, String value) {
		return stringRedisTemplate.opsForValue().setIfAbsent(key, value);
	}

	/**
	 * set key with value and return old value
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public String getSet(String key, String value) {
		return stringRedisTemplate.opsForValue().getAndSet(key, value);
	}

	/**
	 * i++
	 *
	 * @param key
	 * @return
	 */
	public Long increment(String key) {
		return stringRedisTemplate.opsForValue().increment(key, 1);
	}

	/**
	 * i=i+step
	 *
	 * @param key
	 * @param step
	 * @return
	 */
	public Long increment(String key, Long step) {
		return stringRedisTemplate.opsForValue().increment(key, step);
	}

	/**
	 * let the key expire in exipreTime seconds
	 *
	 * @param key
	 * @param exipreTime
	 * @return
	 */
	public Boolean expire(String key, int exipreTime) {
		return stringRedisTemplate.expire(key, exipreTime, TimeUnit.SECONDS);
	}


	/**
	 * add lock
	 * http://mp.weixin.qq.com/s/qJK61ew0kCExvXrqb7-RSg
	 *
	 * @param key        锁
	 * @param value      请求标识
	 * @param expireTime 超期时间,单位ms
	 * @return 是否获取成功
	 * <p>
	 * jedis.set(String key, String value, String nxxx, String expx, int time)，这个set()方法一共有五个形参：
	 * 第一个为key，我们使用key来当锁，因为key是唯一的。
	 * 第二个为value，为什么还要用到value？原因就在于考虑可靠性，解锁的必须是持有锁的请求对象，通过给value赋值，我们就知道这把锁是哪个请求加的了，在解锁的时候就可以有依据。
	 * 第三个为nxxx，这个参数我们填的是NX，意思是SET IF NOT EXIST，即当key不存在时，我们进行set操作；若key已经存在，则不做任何操作；
	 * 第四个为expx，这个参数我们传的是PX，意思是我们要给这个key加一个过期的设置，具体时间由第五个参数决定。
	 * 第五个为time，与第四个参数相呼应，代表key的过期时间。
	 * <p>
	 * 总的来说，执行上面的set()方法就只会导致两种结果：
	 * 1.当前没有锁（key不存在），那么就进行加锁操作，并对锁设置个有效期，同时value表示加锁的客户端。
	 * 2.已有锁存在，不做任何操作。
	 * <p>
	 * 加锁代码满足我们可靠性里描述的三个条件。
	 * 首先，set()加入了NX参数，可以保证如果已有key存在，则函数不会调用成功 ，也就是只有一个客户端能持有锁，满足互斥性。
	 * 其次，由于我们对锁设置了过期时间，即使锁的持有者后续发生崩溃而没有解锁， 锁也会因为到了过期时间而自动解锁（即key被删除），不会发生死锁。
	 * 最后，因为我们将value赋值为requestId，代表加锁的客户端请求标识， 那么在客户端在解锁的时候就可以进行校验是否是同一个客户端。
	 * 由于我们只考虑Redis单机部署的场景，所以容错性我们暂不考虑。
	 */
	public boolean lock(String key, String value, int expireTime) {
		boolean isLocked = false;
		try {
			String result = jedis.set(key, value, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
			if (LOCK_SUCCESS.equals(result)) {
				isLocked = true;
			}
		} catch (Exception e) {
			logger.error("redis加锁异常:{}", e.getMessage());
		}
		return isLocked;

	}

	/**
	 * release lock
	 *
	 * @param key   锁
	 * @param value 请求标识
	 * @return 是否释放成功
	 * <p>
	 * 第一行代码，写了一个简单的Lua脚本代码，将Lua代码传到jedis.eval()方法里，并使参数KEYS[1]赋值为key，
	 * ARGV[1]赋值为value。eval()方法是将Lua代码交给Redis服务端执行。 这段Lua代码的功能是:
	 * 首先获取锁对应的value值，检查是否与传入的value相等，如果相等则删除锁（解锁）。
	 * 因为要确保上述操作是原子性的,所以选择lua语言实现。
	 * 执行eval()方法可以确保原子性，源于Redis的特性.简单来说，就是在eval命令执行Lua代码
	 * 的时候，Lua代码将被当成一个命令去执行，并且直到eval命令执行完成，Redis才会执行其他命令。
	 */
	public boolean unLock(String key, String value) {
		boolean isUnlocked = false;
		try {
			//获取锁对应的value值，检查是否与requestId相等，如果相等则删除锁（解锁);eval()方法执行Lua脚本是原子性的
			String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
			Object result = jedis.eval(script, Collections.singletonList(key), Collections.singletonList(value));
			if (result != null && RELEASE_SUCCESS.equals(result.toString())) {
				isUnlocked = true;
			}
		} catch (Exception e) {
			logger.error("redis解锁异常:{}", e.getMessage());
		}
		return isUnlocked;
	}


}
