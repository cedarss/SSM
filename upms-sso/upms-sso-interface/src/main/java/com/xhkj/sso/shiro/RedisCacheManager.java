package com.xhkj.sso.shiro;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Resource;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author cedar
 */
public class RedisCacheManager implements CacheManager {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private RedisCache redisCache;
	
	private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<String, Cache>();
	
	@Override
	public <K, V> Cache<K, V> getCache(String cacheName) throws CacheException {
		log.debug("获取名称为: " + cacheName + " 的RedisCache实例");
		
		Cache c = caches.get(cacheName);
		
		if (c == null) {
			caches.put(cacheName, redisCache);
			return redisCache;
		}
		return c;
	}

}
