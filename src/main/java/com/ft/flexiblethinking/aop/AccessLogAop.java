package com.ft.flexiblethinking.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class AccessLogAop {
    ThreadLocal<Long> start = new ThreadLocal<>();

    @Pointcut("@annotation(com.ft.flexiblethinking.annotation.AccessLog)")
    private void pointcut(){}

    @Before("pointcut()")
    public void Before(JoinPoint jp) {
        String type = jp.getSignature().getDeclaringType().toString();
        String method = jp.getSignature().getName();
        String[] param = ((MethodSignature) jp.getSignature()).getParameterNames();
        System.out.println(type + "#" + method);
        for (String p : param)
            System.out.println(p);
        start.set(System.currentTimeMillis());
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint jp) {
        String[] paras = ((MethodSignature) jp.getSignature()).getParameterNames();
        Object[] args = jp.getArgs();
        assert(paras.length == args.length);
        for (int i = 0; i < paras.length; i++) {
            System.out.println(paras[i] + " : " + args[i]);
        }
        Object ret = null;
        try {
            ret = jp.proceed(args);
        } catch (Throwable e) {
            ret = e;
        }
        System.out.println(ret);
        return ret;
    }

    @AfterReturning(pointcut = "pointcut()", returning = "result")
    public void afterReturning(JoinPoint jp, Object result) {
        long elapsed = System.currentTimeMillis() - start.get();
        System.out.println("Elapsed Time: " + elapsed + " us");
        System.out.println("Http response content: " + result.toString());
    }
}
