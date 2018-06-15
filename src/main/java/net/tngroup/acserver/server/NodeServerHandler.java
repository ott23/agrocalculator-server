package net.tngroup.acserver.server;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.tngroup.acserver.services.NodeService;
import org.springframework.stereotype.Service;

@Service
@Sharable
public class NodeServerHandler extends SimpleChannelInboundHandler<String> {

    private NodeService nodeService;

    public NodeServerHandler(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) {
        nodeService.readMessage(ctx.channel(), msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) { nodeService.connected(ctx.channel()); }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        nodeService.disconnected(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
