package example.largecapacitydatastreaming.support.aop.aspect;

import example.largecapacitydatastreaming.support.aop.TimeTrace;
import example.largecapacitydatastreaming.support.aop.TraceStatus;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class TimeTraceAspect {

    private final TimeTrace timeTrace;

    @Pointcut("@within(example.largecapacitydatastreaming.support.aop.pointcut.TimeTracer))")
    private void enableLogger(){};

    public TimeTraceAspect(TimeTrace timeTrace) {
        this.timeTrace = timeTrace;
    }

    @Around("enableLogger()")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {

        TraceStatus status;

        try {
            status = timeTrace.start(joinPoint.getSignature().toShortString());

            Object proceed = joinPoint.proceed();

            timeTrace.end(joinPoint.getSignature().toShortString(), status);

            return proceed;
        } catch (Throwable e) {
            throw e;
        }
    }


}
