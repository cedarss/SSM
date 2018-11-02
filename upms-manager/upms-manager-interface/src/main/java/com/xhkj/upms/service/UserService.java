package com.xhkj.upms.service;

import com.xhkj.common.pojo.Result;
import com.xhkj.upms.entity.User;

public interface UserService {

    Result addUser(User user);
    
    User searchUserByAccount(String account);



}
