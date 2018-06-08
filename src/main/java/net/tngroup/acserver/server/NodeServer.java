package net.tngroup.acserver.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.tngroup.acserver.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NodeServer implements Runnable {

    @Value("${node.server.port:33333}")
    private int port;

    @Autowired
    NodeServerSocketInitializer nodeServerSocketInitializer;

    @Autowired
    ClientService clientService;

    public NodeServer() {}

    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(nodeServerSocketInitializer);

            bootstrap.bind(port).sync().channel().closeFuture().sync();

            while (!Thread.currentThread().isInterrupted()) {
                clientService.performTasks();
                Thread.sleep(1000);
            }

        } catch (InterruptedException e) {
            System.out.println("Server interrupted");
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
