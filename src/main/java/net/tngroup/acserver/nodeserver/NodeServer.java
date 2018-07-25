package net.tngroup.acserver.nodeserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.tngroup.acserver.nodeserver.components.InputMessageComponent;
import net.tngroup.acserver.nodeserver.components.StatusComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:node.properties")
public class NodeServer {

    @Value("${node.server.port}")
    private int port;

    private StatusComponent statusComponent;
    private InputMessageComponent inputMessageComponent;

    @Autowired
    public NodeServer(StatusComponent statusComponent,
                      InputMessageComponent inputMessageComponent) {
        this.statusComponent = statusComponent;
        this.inputMessageComponent = inputMessageComponent;
    }

    public void createBootstrap() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new NodeServerSocketInitializer(statusComponent, inputMessageComponent));
        bootstrap.bind(port).sync();
    }

}
