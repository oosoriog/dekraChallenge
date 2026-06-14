# Rule 08 — Observability: AOP Execution-Time Logging

## Status: MANDATORY (Phase 6)

The assessment **requires** an aspect that logs the execution time of each
application method. This is not optional — it is a stated requirement.

> **Dependency gate:** `spring-boot-starter-aspectj` (Spring Boot 4's AOP starter; the old
> `spring-boot-starter-aop` does not exist in Boot 4) is NOT in pom.xml yet. Adding it is
> the first task of Phase 6 and **requires approval**.

---

## Approach

- Implement a Spring AOP aspect using `@Around` advice.
- Measure elapsed time of method execution.
- Log the class name, method name and elapsed time (milliseconds) using `@Slf4j`.
- Apply to all application service/controller methods (or all public methods in relevant packages).

### Example Pointcut

```java
@Around("execution(* com.dekraChallenge.dekra_challenge..*(..))")
public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    long start = System.currentTimeMillis();
    Object result = joinPoint.proceed();
    long elapsed = System.currentTimeMillis() - start;
    log.info("{}.{} executed in {} ms",
        joinPoint.getSignature().getDeclaringTypeName(),
        joinPoint.getSignature().getName(),
        elapsed);
    return result;
}
```

---

## What Changed From Previous Context

- **Previous decision:** Observability was phase-gated and optional (Micrometer, Prometheus, Grafana).
- **Current decision:** AOP execution-time logging is mandatory because the assessment requires it.
- Heavy observability stacks (Prometheus, Grafana, OpenTelemetry) remain irrelevant to this assessment.

---

## Testing

- Write a test that verifies the aspect is applied and logs execution time.
- This can be an integration test that checks log output or uses a test logger appender.

---

## What NOT to Do

- Do NOT add Micrometer, Prometheus, Grafana or OpenTelemetry.
- Do NOT add structured JSON logging unless explicitly requested.
- Keep the aspect simple: measure time, log it. Nothing more.
