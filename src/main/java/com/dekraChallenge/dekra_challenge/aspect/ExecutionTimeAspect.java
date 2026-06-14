package com.dekraChallenge.dekra_challenge.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ExecutionTimeAspect {

    @Pointcut(
            "within(com.dekraChallenge.dekra_challenge.application..*) "
                    + "|| within(com.dekraChallenge.dekra_challenge.adapter..*)")
    public void applicationAndAdapterMethods() {
    }

    @Around("applicationAndAdapterMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startNanos = System.nanoTime();
        boolean success = false;
        try {
            Object result = joinPoint.proceed();
            success = true;
            return result;
        } finally {
            long elapsedMs = (System.nanoTime() - startNanos) / 1_000_000L;
            log.info("{}.{} executed in {} ms (status={})",
                    joinPoint.getSignature().getDeclaringType().getSimpleName(),
                    joinPoint.getSignature().getName(),
                    elapsedMs,
                    success ? "OK" : "FAILED");
        }
    }
}
