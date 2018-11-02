package com.xhkj.common.jedis;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author cedar
 */
public class JedisClientPool<K, V> implements JedisClient {

	// 通过jedis连接池来获取redis的链接
	private JedisPool jedisPool;
	
	public JedisPool getJedisPool() {
		return jedisPool;
	}

	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	private Jedis getResource() {
		return jedisPool.getResource();
	}

	/**
	 * 设置缓存
	 */
	public byte[] set(byte[] key, byte[] value) {
		Jedis jedis = getResource();

		try {
			jedis.set(key, value);
			return value;
		} finally {
			jedis.close();
		}
	}

	public void expire(byte[] key, int i) {
		Jedis jedis = getResource();
		try {
			jedis.expire(key, i);
		} finally {
			jedis.close();
		}
	}

	public byte[] getValue(byte[] key) {
		Jedis jedis = getResource();

		try {
			return jedis.get(key);
		} finally {
			jedis.close();
		}
	}

	public void del(byte[] key) {
		Jedis jedis = getResource();

		try {
			jedis.del(key);
		} finally {
			jedis.close();
		}
	}

	@SuppressWarnings("unchecked")
	public Set<K> keys(String prefix) {
		Jedis jedis = getResource();

		try {
			return (Set<K>) jedis.keys((prefix + "*").getBytes());
		} finally {
			jedis.close();
		}
	}

	public int size(String prefix) {
		Jedis jedis = getResource();

		try {
			return jedis.keys((prefix + "*").getBytes()).size();
		} finally {
			jedis.close();
		}
	}

	/**
	 * flush
	 */
	public void flushDB() {
		Jedis jedis = getResource();
		try {
			jedis.flushDB();
		} finally {
			jedis.close();
		}
	}

	@Override
	public String set(String key, String value) {
		Jedis jedis = getResource();
		try {
			return jedis.set(key, value);
		} finally {
			jedis.close();
		}
	}

	@Override
	public String get(String key) {
		Jedis jedis = getResource();
		try {
			return jedis.get(key);
		} finally {
			jedis.close();
		}
	}

	@Override
	public Boolean exists(String key) {
		Jedis jedis = getResource();
		try {
			return jedis.exists(key);
		} finally {
			jedis.close();
		}
	}

	@Override
	public Long expire(String key, int seconds) {
		Jedis jedis = getResource();
		try {
			return jedis.expire(key, seconds);
		} finally {
			jedis.close();
		}
	}

	@Override
	public Long ttl(String key) {
		Jedis jedis = getResource();
		try {
			return jedis.ttl(key);
		} finally {
			jedis.close();
		}
	}

	@Override
	public Long incr(String key) {
		Jedis jedis = getResource();
		try {
			return jedis.incr(key);
		} finally {
			jedis.close();
		}
	}

	@Override
	public Long hset(String key, String field, String value) {
		Jedis jedis = getResource();
		try {
			return jedis.hset(key, field, value);
		} finally {
			jedis.close();
		}
	}

	@Override
	public String hget(String key, String field) {
		Jedis jedis = getResource();
		try {
			return jedis.hget(key, field);
		} finally {
			jedis.close();
		}
	}

	@Override
	public Long hdel(String key, String... field) {
		Jedis jedis = getResource();
		try {
			return jedis.hdel(key, field);
		} finally {
			jedis.close();
		}
	}

	@Override
	public Boolean hexists(String key, String field) {
		Jedis jedis = getResource();
		try {
			return jedis.hexists(key, field);
		} finally {
			jedis.close();
		}
	}

	@Override
	public List<String> hvals(String key) {
		Jedis jedis = getResource();
		try {
			return jedis.hvals(key);
		} finally {
			jedis.close();
		}
	}

	@Override
	public Long del(String key) {
		Jedis jedis = getResource();
		try {
			return jedis.del(key);
		} finally {
			jedis.close();
		}
	}

}
