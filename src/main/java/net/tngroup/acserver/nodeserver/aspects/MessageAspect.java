package net.tngroup.acserver.nodeserver.aspects;

import io.netty.channel.Channel;
import net.tngroup.acserver.databases.h2.models.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MessageAspect {

    private final Logger logger = LogManager.getFormatterLogger("CommonLogger");

    @Pointcut("execution(public * net.tngroup.acserver.nodeserver.components.InputMessageComponent.readMessage(..))")
    public void inputMessagePointcut() {
    }

    @Pointcut("execution(public * net.tngroup.acserver.nodeserver.components.OutputMessageComponent.sendMessage(..))")
    public void outputMessagePointcut() {
    }

    @Around(value = "inputMessagePointcut()")
    public Message inputMessageMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        Channel channel = (Channel) proceedingJoinPoint.getArgs()[0];

        logger.info("Message from client `" + channel.remoteAddress().toString() + "` received...");

        Message message = (Message) proceedingJoinPoint.proceed();

        if (message == null) return null;

        StringBuilder sb = new StringBuilder()
                .append("Message from client `")
                .append(channel.remoteAddress().toString())
                .append("` successfully handled: `")
                .append(message.getType())
                .append("`");

        logger.info(sb.toString());

        return message;
    }

    @AfterThrowing(value = "inputMessagePointcut()", throwing = "ex")
    public void inputMessageThrowing(Throwable ex) {
        logger.warn("Exception during message receiving: " + ex.getMessage());
    }

    @AfterReturning(value = "outputMessagePointcut()")
    public void outputMessageMethod(JoinPoint joinPoint) {
        Channel channel = (Channel) joinPoint.getArgs()[0];
        Message message = (Message) joinPoint.getArgs()[2];
        logger.info("Message to client `" + channel.remoteAddress().toString() + "` sent: `" + message.getType() + "`");
    }

    @AfterThrowing(value = "outputMessagePointcut()", throwing = "ex")
    public void outputMessageThrowing(Throwable ex) {
        logger.warn("Exception during message sending: " + ex.getMessage());
    }

}
