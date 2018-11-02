package com.xhkj.sso.shiro;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.SerializationUtils;

import com.xhkj.common.jedis.JedisClient;


/**
 * 通过实现AbstractSessionDAO来获取redis的增删改查以及管理活动的链接
 * @author cedar
 *
 */
public class RedisSessionDao extends AbstractSessionDAO {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	
	@Resource
	private JedisClient jedisClient;
	
	@Value("${REDIS_CACHE_PREFIX}")
	private String REDIS_CACHE_PREFIX;
	@Value("${REDIS_CACHE_EXPIRE}")
	private Integer REDIS_CACHE_EXPIRE;
	
	
	
	private byte[] getKey(String key){
		return (REDIS_CACHE_PREFIX+key).getBytes();
	}
	
	private void saveSession(Session session){
		if(session != null && session.getId() != null){
			byte[] key = getKey(session.getId().toString());
			
			//序列化
			byte[] value = SerializationUtils.serialize(session);
			
			//存储到redis缓存中
			jedisClient.set(key,value);
			
			//设置超时时间 1分钟600秒
			jedisClient.expire(key,REDIS_CACHE_EXPIRE);
		}
	}
  	
	/**
	 * 创建Session 
	 * 通过sessionId和前缀组合
	 */
	@Override
	public Serializable doCreate(Session session) {
		//获取sessionId
		Serializable sessionId = generateSessionId(session);
		
		//生成sessionId后  需要将session和sessionId进行捆绑
		assignSessionId(session, sessionId);
		
		byte[] key = getKey(session.getId().toString());
		
		//序列化
		byte[] value = SerializationUtils.serialize(session);
		
		//存储到redis缓存中
		jedisClient.set(key,value);
		
		//设置超时时间 1分钟600秒
		jedisClient.expire(key,REDIS_CACHE_EXPIRE);
		
		return sessionId;
	}
	
	/**
	 * 获取Session
	 */
	@Override
	protected Session doReadSession(Serializable sessionId) {
		if(sessionId == null){
			return null;
		}
		
		byte[] key = getKey(sessionId.toString());
		
		byte[] value = jedisClient.getValue(key);
		ShiroSession session = (ShiroSession)SerializationUtils.deserialize(value);
		//反序列化
		return session;
	}


	/**
	 *更新session
	 *
	 */
	@Override
	public void update(Session session) throws UnknownSessionException {
		saveSession(session);
	}

	/**
	 * 
	 * 删除session
	 */
	@Override
	public void delete(Session session) {
		if(session == null || session.getId() == null){
			return;
		}
		byte[] key = getKey(session.getId().toString());
		jedisClient.del(key);
	}

	/**
	 * 
	 * 获得指定的存活session
	 */
	@Override
	public Collection<Session> getActiveSessions() {
		Set<byte[]> keys = jedisClient.keys(REDIS_CACHE_PREFIX);
		Set<Session> sessions = new HashSet<Session>();
		
		if(CollectionUtils.isEmpty(keys)){
			return sessions;
		}
		
		
		for (byte[] key : keys) {
			Session session = (Session) SerializationUtils.deserialize(jedisClient.getValue(key));
			sessions.add(session);
		}
		
		return sessions;
	}

	public JedisClient getJedisClient() {
		return jedisClient;
	}

	public void setJedisClient(JedisClient jedisClient) {
		this.jedisClient = jedisClient;
	}

	public String getREDIS_CACHE_PREFIX() {
		return REDIS_CACHE_PREFIX;
	}

	public void setREDIS_CACHE_PREFIX(String rEDIS_CACHE_PREFIX) {
		REDIS_CACHE_PREFIX = rEDIS_CACHE_PREFIX;
	}

	public Integer getREDIS_CACHE_EXPIRE() {
		return REDIS_CACHE_EXPIRE;
	}

	public void setREDIS_CACHE_EXPIRE(Integer rEDIS_CACHE_EXPIRE) {
		REDIS_CACHE_EXPIRE = rEDIS_CACHE_EXPIRE;
	}

	
	

	
}
