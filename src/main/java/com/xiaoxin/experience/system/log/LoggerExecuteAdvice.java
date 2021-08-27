package com.xiaoxin.experience.system.log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * create by xiaoxin on 2021/3/13
 */
@Aspect
@Order(0)
@Component
public class LoggerExecuteAdvice {

    @Pointcut(value = "@annotation(LogExecute)")
    public void executorPointcut()
    {
        // do nothing because is pointcut
    }

    @Around(value = "executorPointcut()")
    public Object around(ProceedingJoinPoint pjp)
            throws Throwable
    {
        MethodAspectHelper helper = new MethodAspectHelper(pjp);
        return helper.logExecute();
    }
}
