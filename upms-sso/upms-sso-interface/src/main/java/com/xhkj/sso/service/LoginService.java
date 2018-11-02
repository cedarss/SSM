package com.xhkj.sso.service;

import com.xhkj.common.pojo.Result;
import com.xhkj.upms.entity.User;

public interface LoginService {

    Result addUser(User user);
    
    User searchUserByAccount(String account);

    Result login(String account,String password, String ip);
    
    Result getUserByToken(String token);
    
    Result logout(String token);

}
