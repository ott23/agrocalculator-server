package net.tngroup.acserver.nodeserver.aspects;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class StatusAspect {

    private final Logger logger = LogManager.getFormatterLogger("CommonLogger");

    @Pointcut("execution(* net.tngroup.acserver.nodeserver.components.StatusComponent.connected(..))")
    public void connectedPointcut() {
    }

    @Pointcut("execution(* net.tngroup.acserver.nodeserver.components.StatusComponent.disconnected(..))")
    public void disconnectedPointcut() {
    }

    @Before(value = "connectedPointcut()")
    public void connectedMethod(JoinPoint joinPoint) {
        ChannelHandlerContext ctx = (ChannelHandlerContext) joinPoint.getArgs()[0];
        Channel channel = ctx.channel();
        logger.info("Client with address `%s`: connected", channel.remoteAddress().toString());
    }

    @Before(value = "disconnectedPointcut()")
    public void disconnectedMethod(JoinPoint joinPoint) {
        ChannelHandlerContext ctx = (ChannelHandlerContext) joinPoint.getArgs()[0];
        Channel channel = ctx.channel();
        logger.info("Client with address `%s`: disconnected", channel.remoteAddress().toString());
    }

}
