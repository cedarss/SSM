package com.xhkj.sso.shiro;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.SerializationUtils;

import com.xhkj.common.jedis.JedisClient;




/**
 * @author cedar
 */
public class RedisCache<K,V>  implements Cache<K,V> {
	
	
	@Resource
	private JedisClient jedisClient;
	
	
	@Value("${REDIS_CACHE_PREFIX}")
	private String REDIS_CACHE_PREFIX;
	@Value("${REDIS_CACHE_EXPIRE}")
	private Integer REDIS_CACHE_EXPIRE;
	
	public RedisCache() {
		super();
	}

	public RedisCache(String rEDIS_CACHE_PREFIX) {
		super();
		REDIS_CACHE_PREFIX = rEDIS_CACHE_PREFIX;
	}
	
	public RedisCache(JedisClient jedisClient) {
		super();
		this.jedisClient = jedisClient;
	}
	
	
	public RedisCache(JedisClient jedisClient, String rEDIS_CACHE_PREFIX) {
		super();
		this.jedisClient = jedisClient;
		REDIS_CACHE_PREFIX = rEDIS_CACHE_PREFIX;
	}
	


	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	public byte[] getKey(K k) throws CacheException {
		
		if(k instanceof String){
			return  (REDIS_CACHE_PREFIX+k).getBytes();
		}
		return SerializationUtils.serialize(k);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public V get(K k) throws CacheException {
		
		log.debug("根据key从redis中获取对象 key["+k+"]");
		
		try{
			if(k == null){
				return null;
			}else{

				byte[] value = jedisClient.getValue(getKey(k));
				if(value != null){
					return (V) SerializationUtils.deserialize(value);
				}
			}
		}catch(Throwable t){
			throw new CacheException(t);
		}
		
		return null;
	}
	
	
	@Override
	public V put(K k, V v) throws CacheException {
		log.debug("根据key从redis中存储 key["+k+"]");
		try{
			byte[] key = getKey(k);
			byte[] value = SerializationUtils.serialize(v);
			jedisClient.set(key, value);
			
			//设置缓存时间
			jedisClient.expire(key, REDIS_CACHE_EXPIRE);
			
			return null;
		}catch(Throwable t){
			throw new CacheException(t);
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public V remove(K k) throws CacheException {
		log.debug("根据key从redis中删除 key["+k+"]");
		try{
			byte[] key = getKey(k);
			byte[] value = jedisClient.getValue(key);
			jedisClient.del(key);
			
			if(value != null){
				return (V) SerializationUtils.serialize(value);
			}
		}catch(Throwable t){
			throw new CacheException(t);
		}
		return null;
	}

	@Override
	public int size() {
		return jedisClient.size(REDIS_CACHE_PREFIX);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Set<K> keys() {
		try{
			Set<byte[]> keys = jedisClient.keys(REDIS_CACHE_PREFIX+"*");
			if (CollectionUtils.isEmpty(keys)) {
	        	return Collections.emptySet();
	        }else{
	        	Set<K> newKeys = new HashSet<K>();
	        	for(byte[] key:keys){
	        		newKeys.add((K)key);
	        	}
	        	return newKeys;
	        }
	    } catch (Throwable t) {
	        throw new CacheException(t);
	    }
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<V> values() {
		try{
			Set<byte[]> keys = jedisClient.keys(REDIS_CACHE_PREFIX+"*");
			 if (!CollectionUtils.isEmpty(keys)) {
	             List<V> values = new ArrayList<V>(keys.size());
	             for (byte[] key : keys) {
						V value = get((K)key);
	                 if (value != null) {
	                     values.add(value);
	                 }
	             }
	             return Collections.unmodifiableList(values);
	         } else {
	             return Collections.emptyList();
	         }
	     } catch (Throwable t) {
	         throw new CacheException(t);
	     }
		
	}

	
	/**
	 * 
	 * 此方法不建议重写,存储各种缓存,全部清除,风险性较大
	 */
	@Override
	public void clear() throws CacheException {
		log.debug("从redis中删除所有元素(没有实现)");
	/*	try{
			jedisClient.flushDB();
	     } catch (Throwable t) {
	         throw new CacheException(t);
	     }*/
	}

	



}
