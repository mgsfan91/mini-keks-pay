package com.example.minikekspay.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ControllerLog {

    @Around("controllerPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        StringBuilder builder = new StringBuilder();
        for (Object arg: args) {
            builder.append(arg);
        }
        String method = joinPoint.getSignature().toShortString();
        log.info("==>> Executing " + method + " with args: " + builder + "<<==");
        long begin = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long end = System.currentTimeMillis();
        log.info("==>> Exit " + method + " with duration: " + (end - begin) + " ms <<==");

        return result;
    }

    @Pointcut("execution(* com.example.minikekspay.service.*.*(..))")
    public void controllerPointcut(){}
}
