package com.xhkj.sso.shiro;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionFactory;
import org.apache.shiro.web.session.mgt.DefaultWebSessionContext;

import com.xhkj.common.utils.IPInfoUtil;

public class ShiroSessionFactory implements SessionFactory {

	@Override
	public Session createSession(SessionContext sessionContext) {
			ShiroSession session = new ShiroSession();
			HttpServletRequest request = (HttpServletRequest)sessionContext.get(DefaultWebSessionContext.class.getName()+".SERVLET_REQUEST");
			session.setHost(IPInfoUtil.getIpAddr(request));
			return session;
		}
	


}
