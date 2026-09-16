package com.chen.framework.datasource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Order(-1)
@Component
public class DataSourceAspect {

    @Around("@within(com.chen.framework.datasource.DataSource) || "
        + "@annotation(com.chen.framework.datasource.DataSource)")
    public Object route(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        DataSource annotation = AnnotatedElementUtils.findMergedAnnotation(method, DataSource.class);
        if (annotation == null) {
            annotation = AnnotatedElementUtils.findMergedAnnotation(
                joinPoint.getTarget().getClass(), DataSource.class);
        }

        DataSourceContextHolder.use(annotation == null ? DataSourceType.PRIMARY : annotation.value());
        try {
            return joinPoint.proceed();
        } finally {
            DataSourceContextHolder.clear();
        }
    }
}
