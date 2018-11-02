package com.xhkj.upms.service.impl;

import com.xhkj.common.annotation.SystemServiceLog;
import com.xhkj.common.exception.CustomException;
import com.xhkj.common.pojo.Result;
import com.xhkj.common.utils.ResultUtil;
import com.xhkj.upms.service.UserService;
import com.xhkj.upms.dao.UserMapper;
import com.xhkj.upms.entity.User;
import com.xhkj.upms.entity.UserExample;
import com.xhkj.upms.entity.UserExample.Criteria;

import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author cedar
 */
/*
 * @RunWith(SpringJUnit4ClassRunner.class)
 * 
 * @ContextConfiguration("/spring/spring-dao.xml")
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;

	public Result checkData(String data, int type) {
		UserExample example = new UserExample();
		Criteria criteria = example.createCriteria();
		// 设置查询条件
		// 1.判断用户名是否可用
		if (type == 1) {
			criteria.andUserNameEqualTo(data);
		} else if (type == 2) {
			criteria.andAccountEqualTo(data);
		// 2判断手机号是否可以使用
		} else if (type == 3) {
			criteria.andPhoneEqualTo(data);
			// 4判断邮箱是否可以使用
		} else if (type == 4) {
			criteria.andEmailEqualTo(data);
		} else {
			return new ResultUtil<Object>().setErrorMsg("参数中包含非法数据");
		}

		// 执行查询
		List<User> list = userMapper.selectByExample(example);
		if (list != null && list.size() > 0) {
			// 查询到数据，返回false
			return new ResultUtil<Object>().setErrorMsg(data + "已存在");
		}
		// 数据可以使用
		return new ResultUtil<Object>().setData(null);
	}

	@Override
	@SystemServiceLog(description = "向数据表添加用户")
	public Result addUser(User user) {
		// 检查数据的有效性
		if (StringUtils.isBlank(user.getUserName())) {
			return new ResultUtil<Object>().setErrorMsg("昵称不能为空");
		}
		// 判断昵称是否重复
		Result result = checkData(user.getUserName(), 1);
		if (!result.isSuccess()) {
			return result;
		}

		// 判断账户是否为空
		if (StringUtils.isBlank(user.getAccount())) {
			return new ResultUtil<Object>().setErrorMsg("账户不能为空");
		}

		// 判断昵称是否重复
		result = checkData(user.getAccount(), 2);
		if (!result.isSuccess()) {
			return result;
		}

		// 判断密码是否为空
		if (StringUtils.isBlank(user.getPassword())) {
			return new ResultUtil<Object>().setErrorMsg("密码不能为空");
		}

		// 判断手机是否为空
		if (StringUtils.isBlank(user.getPhone())) {
			return new ResultUtil<Object>().setErrorMsg("手机号不能为空");
		}
		// 是否重复校验
		result = checkData(user.getPhone(), 3);
		if (!result.isSuccess()) {
			return result;
		}

		// 判断邮箱是否为空
		if (StringUtils.isBlank(user.getEmail())) {
			return new ResultUtil<Object>().setErrorMsg("邮箱不能为空");
		}

		// 如果email不为空的话进行是否重复校验
		// if (StringUtils.isNotBlank(user.getEmail())) {
		// 是否重复校验
		result = checkData(user.getEmail(), 4);
		if (!result.isSuccess()) {
			return result;
		}
		// }
		// 补全pojo的属性
		user.setCreateTime(new Date());
		if (user.getIsEnable() == null) {
			user.setIsEnable(1);
		}

		// 密码要进行md5加密
		encryptPassword(user);
		if (userMapper.insertSelective(user) != 1) {
			throw new CustomException("添加用户失败");
		}
		user.setPassword(null);
		user.setPasswordRandom(null);
		return new ResultUtil<User>().setData(user);
	}

	@Override
	public User searchUserByAccount(String account) {
		UserExample example = new UserExample();
		example.createCriteria().andAccountEqualTo(account);
		List<User> list = userMapper.selectByExample(example);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		return list.get(0);
	}

	/**
	 * MD5加密
	 * 
	 * @param user
	 */
	private void encryptPassword(User user) {
		user.setPasswordRandom(String.valueOf(Math.random()));
		Md5Hash md5 = new Md5Hash(user.getPassword(), user.getPasswordRandom());
		user.setPassword(md5.toHex());
	}


	@Test
	public void testRegister() {
		User user = new User();
		user.setUserName("张三");
		user.setAccount("sxs123");
		user.setPassword("123456");
		user.setEmail("1@qq.com");
		user.setPhone("13012345678");
		Result result = addUser(user);
		System.out.println(result.getMessage());
	}


}
