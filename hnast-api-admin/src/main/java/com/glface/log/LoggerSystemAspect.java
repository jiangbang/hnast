package com.glface.log;

import com.alibaba.fastjson.JSON;
import com.glface.common.utils.IpAddress;
import com.glface.model.SysLog;
import com.glface.modules.mapper.LogMapper;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

@Aspect
@Component
@Slf4j
public class LoggerSystemAspect {

    @Resource
    private LogMapper logMapper;

    /**
     * 此处定义切点@Poincut
     * 在有LoggerSystem注解的地方进行代码切入
     */
    @Pointcut("@annotation(com.glface.log.LoggerMonitor)")
    public void loggerPointCut(){
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")//连接点是@RequestMapping注解的方法
    private void webPointcut() {}

    @AfterThrowing(pointcut = "webPointcut()", throwing = "e")//切点在webpointCut()
    public void handleThrowing(JoinPoint joinPoint, Exception e) {//controller类抛出的异常在这边捕获
        saveLog(joinPoint,e);
    }
    @AfterReturning("loggerPointCut()")
    public void returnProcess(JoinPoint joinPoint) {
        saveLog(joinPoint,null);
    }

    public void saveLog(JoinPoint joinPoint, Exception e){
        //有无日志监控注解，有则输出
        String methodName = joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()";
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method targetMethod = methodSignature.getMethod();
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(targetMethod.isAnnotationPresent(LoggerMonitor.class)) {
            log.info("**********Method: {}ms**********", methodName);
        }
        //创建自定义日志对象
        SysLog sysLog = new SysLog();

        //从切面织入点处通过反射机制获取织入点处的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取切入点所在的方法
        Method method = signature.getMethod();
        //此处获取注解方法
        LoggerMonitor loggerSystem = method.getAnnotation(LoggerMonitor.class);
        if (loggerSystem != null) {
            String value = loggerSystem.value();
            //将当前注解中注解的功能说明塞入日志对象中
            sysLog.setTitle(value);
        }
        //获取请求的类名
        String className = joinPoint.getTarget().getClass().getName();
        //获取请求的方法名
        sysLog.setMethod(className + "." + method.getName());
        //请求的参数
        Object[] args = joinPoint.getArgs();
        StringBuffer sb = new StringBuffer();
        for (Object o : args) {
            sb.append(o);
        }
        //将参数所在的数组转换成json
        sysLog.setParams(sb.toString());
        Date now = new Date();
        sysLog.setCreateDate(now);
        sysLog.setCreateBy(com.glface.modules.sys.utils.UserUtils.getUserId());
        //获取用户ip地址
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        sysLog.setRemoteAddr(IpAddress.getIpAddress(request));
        sysLog.setRequestUri(request.getRequestURI());
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        sysLog.setUserAgent(userAgent.toString());
        if(e!=null){
            sysLog.setException(e.getMessage());
        }
        //调用service保存SysLog实体类到数据库
        logMapper.insert(sysLog);
    }
}
