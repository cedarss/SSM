package com.xhkj.upms.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.ShiroHttpSession;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.xhkj.common.annotation.SystemControllerLog;
import com.xhkj.common.pojo.Result;
import com.xhkj.common.utils.IPInfoUtil;
import com.xhkj.common.utils.ResultUtil;
import com.xhkj.sso.service.LoginService;
import com.xhkj.sso.shiro.utils.CookieUtils;
import com.xhkj.upms.dto.fron.DtoUser;
import com.xhkj.upms.entity.User;
import com.xhkj.upms.service.UserService;
import com.xhkj.upms.service.mq.MessageProducer;

import cn.hutool.json.JSONUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author cedar
 *
 */
@RestController
@Api(description = "用户管理")
public class UserController {

	private Logger log= LogManager.getLogger(UserController.class.getName());

    @Autowired
    private UserService userService;
    
    @Autowired
    private MessageProducer mq;
   
    @Autowired
    private LoginService loginService;
    
    @RequestMapping(value = "/user/register",method = RequestMethod.POST)
    @ApiOperation(value="用户注册",notes = "账户,密码是必输项,密码必须是数字")
    @ApiImplicitParams({
    	@ApiImplicitParam(name="userName",value="昵称",required=true,paramType="form"),
    	@ApiImplicitParam(name="account",value="账户",required=true,paramType="form"),
	    @ApiImplicitParam(name="password",value="密码",required=true,paramType="form"),
	    @ApiImplicitParam(name="phone",value="手机号",required=true,paramType="form"),
	    @ApiImplicitParam(name="email",value="邮箱",required=true,paramType="form"),
	})
    @SystemControllerLog(description="用户注册")
    public Result<Object> addUser(@ModelAttribute User user){
//        Result result = userService.addUser(user);
    	Result result = loginService.addUser(user);
    	if(result.isSuccess()){
    		try {
    			//发送邮件
				mq.send(JSONUtil.toJsonStr(result.getResult()));
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
        return result;
    }
    
    @RequestMapping(value = "/user/test",method = RequestMethod.GET)
    public void testMq(){
    	try {
			mq.send("/n/n用户注册/n/n");
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value="/user/login",method = RequestMethod.POST)
    @ApiOperation(value="用户登录",notes = "账户密码是必须项")
    @ApiImplicitParams({
	    @ApiImplicitParam(name="accid",value="账户",required=true,paramType="form"),
	    @ApiImplicitParam(name="accpwd",value="密码",required=true,paramType="form"),
	})
    @SystemControllerLog(description="登录系统")
    public Result<Object> login(String account,String password,boolean rememberMe,HttpServletRequest request,HttpServletResponse response){
      log.info("/n/n用户登录:"+account+"-"+password+"-"+rememberMe);
      //1.SSO登录
      
      Result result = loginService.login(account,password,IPInfoUtil.getIpAddr(request));
      
      
      if(result.isSuccess()){
    	  DtoUser dtoUser = (DtoUser) result.getResult();
	      CookieUtils.createCookie(dtoUser.getToken(), request, response);
	      result.setResult(null);
	      return result;
      }else{
    	  return result;
      }
      
	      
        
       
      
      //2.普通登录
      /*
	   Subject subject = SecurityUtils.getSubject();
       UsernamePasswordToken token = new UsernamePasswordToken(account,password);
       //是否记住用户
       token.setRememberMe(rememberMe);
       
	   try{
		   subject.login(token);
		   return new ResultUtil<Object>().setData(null);
	   }catch(Exception e){
		   e.printStackTrace();
		   return new ResultUtil<Object>().setErrorMsg("账号或密码错误");
	   }
	   */
    }
    
   
}
