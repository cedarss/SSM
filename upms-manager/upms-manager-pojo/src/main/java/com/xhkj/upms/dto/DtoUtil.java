package com.xhkj.upms.dto;

import com.xhkj.upms.dto.fron.DtoUser;
import com.xhkj.upms.entity.User;

public class DtoUtil {

	
	
	public static DtoUser UserConvertUserDto(User user){
		DtoUser dtoUser = new DtoUser();
		dtoUser.setId(user.getId());
		dtoUser.setUserName(user.getUserName());
		dtoUser.setAccount(user.getAccount());
		dtoUser.setEmail(user.getEmail());
		dtoUser.setPhone(user.getPhone());
		dtoUser.setIsEnable(user.getIsEnable());
		return dtoUser;
	}
}
