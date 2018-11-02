package com.xhkj.sso.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.xhkj.common.annotation.SystemServiceLog;
import com.xhkj.common.exception.CustomException;
import com.xhkj.common.pojo.Result;
import com.xhkj.common.utils.ResultUtil;
import com.xhkj.sso.service.LoginService;
import com.xhkj.sso.shiro.CustomRealm;
import com.xhkj.sso.shiro.RedisSessionDao;
import com.xhkj.sso.shiro.ShiroSession;
import com.xhkj.upms.dao.UserMapper;
import com.xhkj.upms.dto.DtoUtil;
import com.xhkj.upms.dto.fron.DtoUser;
import com.xhkj.upms.entity.User;
import com.xhkj.upms.entity.UserExample;
import com.xhkj.upms.entity.UserExample.Criteria;

import cn.hutool.json.JSONUtil;

/**
 * @author cedar
 */

@Service
public class LoginServiceImpl implements LoginService {

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private RedisSessionDao redisSessionDao;

	@Value("${REDIS_CACHE_PREFIX}")
	private String REDIS_CACHE_PREFIX;

	@Value("${REDIS_CACHE_EXPIRE}")
	private Integer REDIS_CACHE_EXPIRE;

	@Value("${SESSION_USER}")
	private String SESSION_USER;

	public Result checkData(String data, int type) {
		UserExample example = new UserExample();
		Criteria criteria = example.createCriteria();
		// 设置查询条件
		// 1.判断用户名是否可用
		if (type == 1) {
			criteria.andUserNameEqualTo(data);
			// 2判断手机号是否可以使用
		} else if (type == 2) {
			criteria.andAccountEqualTo(data);
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
		
		if(user.getPassword().length() < 6){
			throw new RuntimeException("密码格式不对");
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
		/*
		result = checkData(user.getEmail(), 4);
		if (!result.isSuccess()) {
			return result;
		}
		*/
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

	@Override
	@SystemServiceLog(description = "用户登录")
	public Result login(String account, String password, String ip) {

		User user = searchUserByAccount(account);
		if (user == null) {
			return new ResultUtil<Object>().setErrorMsg("登录名不存在");
		} else if (!user.isEnable()) {
			return new ResultUtil<Object>().setErrorMsg("已被用户禁用");
		} else {
			Md5Hash md5 = new Md5Hash(password, user.getPasswordRandom());
			
			if (!md5.toHex().equals(user.getPassword())) {
				return new ResultUtil<Object>().setErrorMsg("用户名或密码不正确");
			}

			
			DtoUser dtoUser = DtoUtil.UserConvertUserDto(user);
			
			String sessionId = doLoginSuccess(new UsernamePasswordToken(account,password),dtoUser,ip);
			
			dtoUser.setToken(sessionId);

			return new ResultUtil<Object>().setData(dtoUser);
		}

	}

	@Override
	@SystemServiceLog(description = "根据token获取用户信息")
	public Result getUserByToken(String token) {

		Session session = redisSessionDao.readSession(token);

		if (session == null) {
			return new ResultUtil<Object>().setErrorMsg("用户登录已过期");
		}

		// 重置过期时间
		redisSessionDao.update(session);
		String json = (String) session.getAttribute(SESSION_USER);
		return new ResultUtil<Object>().setData(JSONUtil.toBean(json, DtoUser.class));
	}

	@Override
	public Result logout(String token) {
		redisSessionDao.delete(redisSessionDao.readSession(token));
		return new ResultUtil<Object>().setData(null);
	}
	
	public String doLoginSuccess(UsernamePasswordToken token,DtoUser dtoUser,String ip){
		
		ShiroSession session = new ShiroSession();
		
		SimplePrincipalCollection spc = new SimplePrincipalCollection();
		
		Object principal = token.getPrincipal();
		
		spc.add(principal, CustomRealm.class.getName());
		
		session.setHost(ip);
		
        session.setAttribute(DefaultSubjectContext.AUTHENTICATED_SESSION_KEY, true);
        
        session.setAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY, spc);
        
        session.setAttribute(SESSION_USER, JSONUtil.toJsonStr(dtoUser));
        
		redisSessionDao.doCreate(session);
		
		return session.getId().toString();
	}



}
