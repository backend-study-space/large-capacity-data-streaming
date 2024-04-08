package example.largecapacitydatastreaming.support.aop.pointcut;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface TimeTracer {
}
