package net.tngroup.acserver.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.tngroup.acserver.services.NodeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class NodeServer implements ApplicationRunner {

    private Logger logger = LogManager.getFormatterLogger("ConsoleLogger");

    @Value("${node.server.port:33333}")
    private int port;

    private NodeService nodeService;

    @Autowired
    public NodeServer(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        logger.info("Server started");

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new NodeServerSocketInitializer(nodeService));
        bootstrap.bind(port).sync();
    }

}
