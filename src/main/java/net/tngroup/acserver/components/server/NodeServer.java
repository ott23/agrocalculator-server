package net.tngroup.acserver.components.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.tngroup.acserver.components.NodeComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NodeServer {

    @Value("${node.server.port:33333}")
    private int port;

    private NodeComponent nodeComponent;

    @Autowired
    public NodeServer(NodeComponent nodeComponent) {
        this.nodeComponent = nodeComponent;
    }

    public void createBootstrap() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new NodeServerSocketInitializer(nodeComponent));
        bootstrap.bind(port).sync();
    }

}
