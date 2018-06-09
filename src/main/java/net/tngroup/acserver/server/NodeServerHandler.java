package net.tngroup.acserver.server;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.tngroup.acserver.services.CalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Sharable
public class NodeServerHandler extends SimpleChannelInboundHandler<String> {

    private CalculatorService calculatorService;

    @Autowired
    public NodeServerHandler(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) {
        calculatorService.readMessage(ctx.channel(), msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        calculatorService.connected(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        calculatorService.disconnected(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
