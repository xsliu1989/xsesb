package com.xs.Intercept;


import com.xs.menu.LogType;
import com.xs.serve.LogUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.aspectj.lang.annotation.*;

/**
 * 用来监控方法的执行时间-- 对应配置文件是spring-servlet.xml
 * PS：必须放到springmvc的配置文件里,放在spring父容器里面由于先初始化的是spring父容器上下文,先实例化的是除@Controller外的bean,所以无法生成代理。
 *
 * @author
 * @version
 * @date
 */
@Component
@Aspect
public class LogIntercept {

    private final static Logger logger = (Logger) LoggerFactory.getLogger(LogIntercept.class);
    private static LogUtil logUtil;



    @Around("execution (* com.xs.controller..*(..)) || "    +
            "execution (* com.xs.service..*(..)) || "       +
            "execution (* com.xs.filter..*(..))  "
    )
    public Object invoke(ProceedingJoinPoint pjp) throws Throwable {



        //监控的类名
        String className = pjp.getTarget().getClass().getName();

        //监控的方法名
        String methodName = pjp.getSignature().getName();

        long startTime=System.currentTimeMillis();
        logUtil.info(LogType.TIME, logger,"[" +className + "]" + "[" +methodName + "]" + " 开始时间:" + startTime);
//
//        //监控的参数
//        Object[] objs  = pjp.getArgs();//获取请求参数

        Object result = null;



        try {
            //这个是我们监控的bean的执行并返回结果
            result = pjp.proceed();
        } catch (Throwable e) {
            throw e;
        }

        long endTime=System.currentTimeMillis();

        logUtil.info(LogType.TIME, logger,"[" +className + "]" + "[" +methodName + "]" + " 结束时间:" + startTime +"  总用时（毫秒）：" + (endTime-startTime));



        return result;

    }




}