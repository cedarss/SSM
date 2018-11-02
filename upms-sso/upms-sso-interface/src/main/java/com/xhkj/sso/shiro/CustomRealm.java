package com.xhkj.sso.shiro;

import java.util.HashSet;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.xhkj.sso.service.LoginService;
import com.xhkj.upms.entity.User;

import io.swagger.annotations.ApiOperation;

/**
 * @author cedar
 */
public class CustomRealm extends AuthorizingRealm {
	/*
	@Autowired
	private UserService userService;
	*/
	@Autowired
	private LoginService loginService;
	
	@ApiOperation(value="权限认证")
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		String userName = (String) principals.getPrimaryPrincipal();
		/**Set<String> roles = getRolesByUserName(userName);*/
		Set<String> permissions = getPermissionsByUserName(userName);
		
		
		SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
		/**authorizationInfo.setRoles(roles);*/
		authorizationInfo.setStringPermissions(permissions);
		return authorizationInfo;
	}

	/**真实项目是在缓存或者数据库中获取*/
	private Set<String> getPermissionsByUserName(String userName) {
		Set<String> sets = new HashSet<>();
		sets.add("user:update");
		sets.add("user:add");
		return sets;
	}


/**	private Set<String> getRolesByUserName(String userName) {
		
		System.out.println("从数据库中获取数据");
		
		RoleUserVOExample example = new RoleUserVOExample();
		example.createCriteria().andAccidEqualTo(userName);
		List<RoleUserVO> list = ruDao.selectByExample(example);
		if(CollectionUtils.isEmpty(list)){
			return null;
		}
		Set<String> roleList = new HashSet<String>();
		for (RoleUserVO vo : list) {
			roleList.add(vo.getRoleName());
		}
		return roleList;
	}*/
	
	
	public User getPasswordByUserName(String userName){
		User user = loginService.searchUserByAccount(userName);
		if(user == null){
			return null;
		}
		return user;
				
	}


	/**用户认证*/
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) 
			throws AuthenticationException {
		
		//1.从主体传过来的认证信息中,获得用户名
		String userName = (String) token.getPrincipal();
		//2.通过用户名到数据库中获取凭证
		User user = getPasswordByUserName(userName);
		if(user == null){
			return null;
		}
		
		SimpleAuthenticationInfo authenticationInfo = 
				new SimpleAuthenticationInfo(userName,user.getPassword(),getName());
		//把盐设置进去
		authenticationInfo.setCredentialsSalt(ByteSource.Util.bytes(user.getPasswordRandom()));
		
		return authenticationInfo;
	}
	
	

}
