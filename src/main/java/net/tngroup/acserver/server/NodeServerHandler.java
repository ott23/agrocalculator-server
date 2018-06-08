package net.tngroup.acserver.server;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.tngroup.acserver.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Sharable
public class NodeServerHandler extends SimpleChannelInboundHandler<String> {

    @Autowired
    ClientService clientService;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        clientService.newActiveClient(ctx.channel());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) {
        clientService.newMessage(ctx.channel(), msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        clientService.newInactiveClient(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
