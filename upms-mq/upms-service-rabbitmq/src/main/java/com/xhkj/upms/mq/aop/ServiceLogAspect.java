package com.xhkj.upms.mq.aop;

import java.lang.reflect.Method;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Component;

import com.xhkj.common.annotation.SystemServiceLog;
import com.xhkj.common.utils.ThreadPoolUtil;
import com.xhkj.upms.entity.DbLog;
import com.xhkj.upms.service.log.SystemService;

import cn.hutool.json.JSONUtil;

/**
 * Spring AOP实现日志管理
 */
@Aspect
@Component
public class ServiceLogAspect {

    private Logger log= LogManager.getLogger(ServiceLogAspect.class.getName());

    private static final ThreadLocal<Date> beginTimeThreadLocal = new NamedThreadLocal<Date>("ThreadLocal beginTime");

    @Autowired
    private SystemService systemService;


    /**
     * Service层切点,注解方式
     */
    @Pointcut("@annotation(com.xhkj.common.annotation.SystemServiceLog)")
    public void serviceAspect() {
        log.info("========ServiceAspect===========");
    }


    
    /**
     * 前置通知 (在方法执行之前返回)用于拦截Controller层记录用户的操作的开始时间
     * @param joinPoint 切点
     * @throws InterruptedException
     */
    @Before("serviceAspect()")
    public void doServiceBefore(JoinPoint joinPoint) throws InterruptedException{

        //线程绑定变量（该数据只有当前请求的线程可见）
        Date beginTime=new Date();
        beginTimeThreadLocal.set(beginTime);
    }


    /**
     * 异常通知 用于拦截service层记录异常日志
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(pointcut="serviceAspect()", throwing="e")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable e) {

        try {

        DbLog dbLog=new DbLog();

        //日志标题
        dbLog.setName(getServiceMethodDescription(joinPoint));
        //日志类型
        dbLog.setType(0);
        //日志请求url
        dbLog.setUrl(joinPoint.getSignature().getDeclaringType().getSimpleName()+"-"+joinPoint.getSignature().getName());
        //请求方式
        dbLog.setRequestType("DUBBO");
        
        Object[] args = joinPoint.getArgs();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < args.length; i++) {
            sb.append(JSONUtil.toJsonStr("" + (i+1) + ":" + args[i]));
        }
        //请求参数
        dbLog.setRequestParam(sb.toString());
       
        //请求开始时间
        Date logStartTime = beginTimeThreadLocal.get();

        long beginTime = beginTimeThreadLocal.get().getTime();
        long endTime = System.currentTimeMillis();
        //请求耗时
        Long logElapsedTime = endTime - beginTime;
        dbLog.setTime(Math.toIntExact(logElapsedTime));
        dbLog.setCreateDate(logStartTime);
        //调用线程保存至数据库
        ThreadPoolUtil.getPool().execute(new SaveSystemLogThread(dbLog,systemService));
        log.error("[异常错误]["+dbLog.toString()+"]", e);
        } catch (Exception e1) {
            log.error("AOP异常通知异常", e1);
        }

    }

    /**
     * 保存日志
     */
    private static class SaveSystemLogThread implements Runnable {
        private DbLog dbLog;
        private SystemService systemService;

        public SaveSystemLogThread(DbLog dbLog, SystemService systemService) {
            this.dbLog = dbLog;
            this.systemService = systemService;
        }

        @Override
        public void run() {

            systemService.addDbLog(dbLog);
        }
    }

    /**
     * 获取注解中对方法的描述信息 用于Service层注解
     * @param joinPoint 切点
     * @return 方法描述
     * @throws Exception
     */
    public static String getServiceMethodDescription(JoinPoint joinPoint) throws Exception{
        //获取目标类名
        String targetName = joinPoint.getTarget().getClass().getName();
        //获取方法名
        String methodName = joinPoint.getSignature().getName();
        //获取相关参数
        Object[] arguments = joinPoint.getArgs();
        //生成类对象
        Class targetClass = Class.forName(targetName);
        //获取该类中的方法
        Method[] methods = targetClass.getMethods();

        String description = "";

        for(Method method : methods) {
            if(!method.getName().equals(methodName)) {
                continue;
            }
            Class[] clazzs = method.getParameterTypes();
            if(clazzs.length != arguments.length) {
                //比较方法中参数个数与从切点中获取的参数个数是否相同，原因是方法可以重载哦
                continue;
            }
            description = method.getAnnotation(SystemServiceLog.class).description();
        }
        return description;
    }

    
}
