package net.tngroup.acserver.nodeserver;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.tngroup.acserver.nodeserver.components.InputMessageComponent;
import net.tngroup.acserver.nodeserver.components.StatusComponent;
import org.springframework.stereotype.Service;

@Service
@Sharable
public class NodeServerHandler extends SimpleChannelInboundHandler<String> {

    private StatusComponent statusComponent;
    private InputMessageComponent inputMessageComponent;

    NodeServerHandler(StatusComponent statusComponent,
                      InputMessageComponent inputMessageComponent) {
        this.statusComponent = statusComponent;
        this.inputMessageComponent = inputMessageComponent;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) {
        inputMessageComponent.readMessage(ctx.channel(), msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) { statusComponent.connected(ctx.channel()); }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        statusComponent.disconnected(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
