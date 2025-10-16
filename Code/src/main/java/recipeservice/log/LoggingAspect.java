package recipeservice.log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @After("execution(* recipeservice..*(..))")
    public void logAfter(JoinPoint joinPoint) {
        logger.info("Executed: {}", joinPoint.getSignature());
    }
}