package net.tngroup.acserver.components.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import net.tngroup.acserver.components.NodeComponent;
import org.springframework.stereotype.Service;

@Service
public class NodeServerSocketInitializer extends ChannelInitializer<SocketChannel> {

    private NodeComponent nodeComponent;

    NodeServerSocketInitializer(NodeComponent nodeComponent) {
        this.nodeComponent = nodeComponent;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();

        //pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new StringEncoder());

        pipeline.addLast(new NodeServerHandler(nodeComponent));
    }
}
