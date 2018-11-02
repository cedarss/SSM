package com.xhkj.sso.shiro.utils;

import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.io.Serializer;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.servlet.ShiroHttpSession;
import org.apache.shiro.web.servlet.SimpleCookie;

import com.xhkj.sso.shiro.CustomRealm;

public class CookieUtils {

	
	   
	 	public static SimpleCookie createCookie(String token,HttpServletRequest request,HttpServletResponse response){
	    	 SimpleCookie cookie = new SimpleCookie(ShiroHttpSession.DEFAULT_SESSION_ID_NAME);
	    	 cookie.setHttpOnly(true);
	         cookie.setPath("/");
	         cookie.setValue(token);
	         cookie.setMaxAge(200000);
	         cookie.saveTo(request, response);
	    	return cookie;
	    }
	 	
	 	/* 没起作用
	 	 * public static SimpleCookie createRememberMeCookie(String username,boolean rememberMe,HttpServletRequest request,HttpServletResponse response){
		        if(!rememberMe){
		        	return null;
		        }
		        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
		    	//这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
		        SimpleCookie cookie = new SimpleCookie("rememberMe");
		        //如果httyOnly设置为true，则客户端不会暴露给客户端脚本代码，使用HttpOnly cookie有助于减少某些类型的跨站点脚本攻击；
		        cookie.setHttpOnly(true);
		        //记住我cookie生效时间,默认30天 ,单位秒：60 * 60 * 24 * 30
		        //7天
		        cookie.setMaxAge(604800);
		        cookie.setPath("/");
		        cookieRememberMeManager.setCipherKey(Base64.decode("25BsmdYwjnfcWmnhAciDDg=="));
		        Serializer<PrincipalCollection> serializer = cookieRememberMeManager.getSerializer();
		        AuthenticationToken token = new UsernamePasswordToken(username,"123456",true);
		        String base64 = Base64.encodeToString(serializer.serialize(new SimplePrincipalCollection(token.getPrincipal(), CustomRealm.class.getName())));
		        cookie.setValue(base64);
		        cookie.saveTo(request, response);
		        return cookie;
		    }
		    */
	 	
	 	public static void main(String[] args) throws NoSuchAlgorithmException {
	 		//rememberme cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度（128 256 512 位），通过以下代码可以获取
	 	    KeyGenerator keygen = KeyGenerator.getInstance("AES");
	 	    SecretKey deskey = keygen.generateKey();
	 	    System.out.println(Base64.encodeToString(deskey.getEncoded()));
		}
}
