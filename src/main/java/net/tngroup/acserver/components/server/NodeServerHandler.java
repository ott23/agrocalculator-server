package net.tngroup.acserver.components.server;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.tngroup.acserver.components.NodeComponent;
import org.springframework.stereotype.Service;

@Service
@Sharable
public class NodeServerHandler extends SimpleChannelInboundHandler<String> {

    private NodeComponent nodeComponent;

    NodeServerHandler(NodeComponent nodeComponent) {
        this.nodeComponent = nodeComponent;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) {
        nodeComponent.readMessage(ctx.channel(), msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) { nodeComponent.connected(ctx.channel()); }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        nodeComponent.disconnected(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
