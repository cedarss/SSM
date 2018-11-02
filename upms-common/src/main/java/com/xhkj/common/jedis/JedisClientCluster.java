package com.xhkj.common.jedis;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

public class JedisClientCluster implements JedisClient {
	
	private JedisCluster jedisCluster;
	

	public JedisCluster getJedisCluster() {
		return jedisCluster;
	}

	public void setJedisCluster(JedisCluster jedisCluster) {
		this.jedisCluster = jedisCluster;
	}

	@Override
	public String set(String key, String value) {
		return jedisCluster.set(key, value);
	}

	@Override
	public String get(String key) {
		return jedisCluster.get(key);
	}

	@Override
	public Boolean exists(String key) {
		return jedisCluster.exists(key);
	}

	@Override
	public Long expire(String key, int seconds) {
		return jedisCluster.expire(key, seconds);
	}

	@Override
	public Long ttl(String key) {
		return jedisCluster.ttl(key);
	}

	@Override
	public Long incr(String key) {
		return jedisCluster.incr(key);
	}

	@Override
	public Long hset(String key, String field, String value) {
		return jedisCluster.hset(key, field, value);
	}

	@Override
	public String hget(String key, String field) {
		return jedisCluster.hget(key, field);
	}

	@Override
	public Long hdel(String key, String... field) {
		return jedisCluster.hdel(key, field);
	}

	@Override
	public Boolean hexists(String key, String field) {
		return jedisCluster.hexists(key, field);
	}

	@Override
	public List<String> hvals(String key) {
		return jedisCluster.hvals(key);
	}

	@Override
	public Long del(String key) {
		return jedisCluster.del(key);
	}

	/**
	 * 设置缓存
	 */
	@Override
	public byte[] set(byte[] key, byte[] value) {
		
		try{
			jedisCluster.set(key, value);
			return value;
		}finally{
			close(jedisCluster);
		}
	}

	@Override
	public void expire(byte[] key, int i) {
		try{
			jedisCluster.expire(key, i);
		}finally{
			close(jedisCluster);
		}
	}


	@Override
	public byte[] getValue(byte[] key) {
		
		try{
			return jedisCluster.get(key); 
		}finally{
			close(jedisCluster);
		}
	}


	@Override
	public void del(byte[] key) {
		
		try{
			jedisCluster.del(key); 
		}finally{
			close(jedisCluster);
		}
	}


	@Override
	public Set<byte[]> keys(String prefix) {
		
		try{
			return  jedisCluster.hkeys((prefix+"*").getBytes());
		}finally{
			close(jedisCluster);
		}
	}
	
	@Override
	public int size(String prefix){
		
		try{
			return jedisCluster.hkeys((prefix+"*").getBytes()).size();
		}finally{
			close(jedisCluster);
		}
	}
	
	
	/**
	 * flush
	 */
	@Override
	public void flushDB(){
		/*
		try{
			jedisCluster.flushDB();
		}finally{
			close(jedisCluster);
		}
		*/
	}
	
	/**
	 * redis链接关闭方法
	 * 有一种说法,使用到连接池, 不需要关闭,但是觉得高并发时,可能不试用
	 *  redis-server会关闭空闲超时的连接
	 *  redis.conf中可以设置超时时间：
	 *	# Close the connection after a client is idle for N seconds (0 to disable)
	 *	timeout 300 
	 * @param jedisCluster
	 */
	public void close(JedisCluster jedisCluster){
		if(jedisCluster != null){
			try {
				jedisCluster.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
