package com.xhkj.common.exception;

public class CustomException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private String msg;
	
	public CustomException(String msg) {
		super();
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	

}
