package com.xhkj.sso.shiro;

import java.io.Serializable;

import org.apache.shiro.session.mgt.SimpleSession;

public class ShiroSession extends SimpleSession implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7869705705211091470L;

	@Override
	public String toString() {
		return "ShiroSession [getId()=" + getId() + ", getStartTimestamp()=" + getStartTimestamp()
				+ ", getStopTimestamp()=" + getStopTimestamp() + ", getLastAccessTime()=" + getLastAccessTime()
				+ ", isExpired()=" + isExpired() + ", getTimeout()=" + getTimeout() + ", getHost()=" + getHost()
				+ ", getAttributes()=" + getAttributes() + ", isStopped()=" + isStopped() + ", isValid()=" + isValid()
				+ ", isTimedOut()=" + isTimedOut() + ", getAttributeKeys()=" + getAttributeKeys() + ", hashCode()="
				+ hashCode() + ", toString()=" + super.toString() + ", getClass()=" + getClass() + "]";
	}
}
