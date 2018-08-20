package net.tngroup.acserver.node.aspects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class NodeAspect {

    private final Logger logger = LogManager.getFormatterLogger("CommonLogger");

    @Pointcut("execution(public * net.tngroup.acserver.node.AutoExecutor.run(..))")
    public void startPointcut() {
    }

    @After(value = "startPointcut()")
    public void startMethod() {
        logger.info("Application initialized");
    }

}
