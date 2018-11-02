package com.xhkj.sso.shiro;

import java.io.Serializable;

import javax.servlet.ServletRequest;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;

/**
 * @author cedar
 */
public class CustomSessionManager extends DefaultWebSessionManager {
	
	/**
	 * SessionKey 里面存储了 Request对象
	 * 我们可以把session放入reqeust对象里  这样就不用每次去redis读取了
	 */
	@Override
	protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {
		Serializable sessionId = getSessionId(sessionKey);
		ServletRequest request = null;
		
		if(sessionKey instanceof WebSessionKey){
			request = ((WebSessionKey)sessionKey).getServletRequest();
		}
		
		//先从request获取
		if(request != null && sessionId != null){
			Session session = (Session)request.getAttribute(sessionId.toString());
			if(session != null){
				return session;
			}
		}
		
		//获取不到在redis获取,并装进request中
		Session session = super.retrieveSession(sessionKey);
		
		if(request != null && sessionId != null){
			request.setAttribute(sessionId.toString(), session);
		}else{
			System.out.println("session == null");
		}
		
		return session;
	}

}
