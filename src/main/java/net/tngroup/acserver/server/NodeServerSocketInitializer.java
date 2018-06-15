package net.tngroup.acserver.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import net.tngroup.acserver.services.NodeService;
import org.springframework.stereotype.Service;

@Service
public class NodeServerSocketInitializer extends ChannelInitializer<SocketChannel> {

    private NodeService nodeService;

    public NodeServerSocketInitializer(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();

        //pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new StringEncoder());

        pipeline.addLast(new NodeServerHandler(nodeService));
    }
}
