package com.xhkj.common.jedis;

import java.util.List;
import java.util.Set;

public interface JedisClient {

	String set(String key, String value);

	String get(String key);

	Boolean exists(String key);

	Long expire(String key, int seconds);

	Long ttl(String key);

	Long incr(String key);

	Long hset(String key, String field, String value);

	String hget(String key, String field);

	Long hdel(String key, String... field);

	Boolean hexists(String key, String field);

	List<String> hvals(String key);

	Long del(String key);
	

	/**
	 * 提供一个获得链接的方法
	 */
	//Jedis getResource();
	
	
	/**
	 * 设置缓存
	 */
	byte[] set(byte[] key, byte[] value);

	void expire(byte[] key, int i);


	byte[] getValue(byte[] key);


	void del(byte[] key);


	<K> Set<K> keys(String prefix);
	
	
	int size(String prefix);
	
	
	/**
	 * flush
	 */
	void flushDB();
	
	
}
