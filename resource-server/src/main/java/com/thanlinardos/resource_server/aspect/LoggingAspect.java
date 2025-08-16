package com.thanlinardos.resource_server.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(2)
@Slf4j
public class LoggingAspect {

    @Around("com.thanlinardos.resource_server.aspect.PointCutDefinitions.forServicePackageAndNotEntityReturned()")
    public Object timeMethodExecution(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long duration = System.currentTimeMillis() - start;
        log.info("Method: {} | Duration: {}ms", proceedingJoinPoint.getSignature(), duration);
        return result;
    }
}
