package com.xhkj.upms.entity;

import java.io.Serializable;
import java.util.Date;



public class User implements Serializable{
	private static final long serialVersionUID = 1L;

	private Integer id;

    private String userName;	//昵称

    private String account;		//账户

    private String password;	//密码

    private String passwordRandom; //盐

    private String email;		//邮箱

    private String phone;		//手机号

    private Date createTime;	//创建时间

    private Integer isEnable;	//是否可用 1是 0否

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
        this.userName = userName == null ? null : userName.trim();
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account == null ? null : account.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getPasswordRandom() {
        return passwordRandom;
    }

    public void setPasswordRandom(String passwordRandom) {
        this.passwordRandom = passwordRandom == null ? null : passwordRandom.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
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
    
    public boolean isEnable() {
        return (isEnable != null && isEnable == 1)?true:false;
    }

    public void setIsEnable(Integer isEnable) {
        this.isEnable = isEnable;
    }

	@Override
	public String toString() {
		return "User [id=" + id + ", userName=" + userName + ", account=" + account + ", password=" + password
				+ ", passwordRandom=" + passwordRandom + ", email=" + email + ", phone=" + phone + ", createTime="
				+ createTime + ", isEnable=" + isEnable + "]";
	}
    
    
}