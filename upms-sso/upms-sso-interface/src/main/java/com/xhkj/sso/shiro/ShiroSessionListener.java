package com.xhkj.sso.shiro;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShiroSessionListener implements SessionListener {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void onExpiration(Session session) {
		// 会话过期时触发
		log.info("ShiroSessionListener session {} 过期:"+session.getId());
	}

	@Override
	public void onStart(Session session) {
		// 会话创建时触发
		log.info("ShiroSessionListener session {} 被创建:"+session.getId());
	}

	@Override
	public void onStop(Session session) {
		// 会话被停止时触发
		log.info("ShiroSessionListener session {} 销毁:"+session.getId());
	}

}
