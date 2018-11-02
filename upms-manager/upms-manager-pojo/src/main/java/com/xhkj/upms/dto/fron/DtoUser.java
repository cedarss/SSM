package com.xhkj.upms.dto.fron;

import java.io.Serializable;
import java.util.Date;

public class DtoUser implements Serializable{
	private static final long serialVersionUID = 1L;

	private Integer id;

    private String userName;	//昵称

    private String account;		//账户

    private String email;		//邮箱

    private String phone;		//手机号

    private Date createTime;	//创建时间

    private Integer isEnable;	//是否可用 1是 0否
    
    private String token;
    
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getIsEnable() {
		return isEnable;
	}

	public void setIsEnable(Integer isEnable) {
		this.isEnable = isEnable;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
