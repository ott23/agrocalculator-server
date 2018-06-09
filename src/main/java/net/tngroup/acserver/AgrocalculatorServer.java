package net.tngroup.acserver;

import net.tngroup.acserver.server.NodeServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class AgrocalculatorServer {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(AgrocalculatorServer.class, args);

        Thread server = new Thread(ctx.getBean(NodeServer.class));
        server.start();
    }
}
