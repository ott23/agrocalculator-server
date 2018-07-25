package net.tngroup.acserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource(value = "classpath:node.properties")
public class AgroCalculatorServer {

    public static void main(String[] args) {
        SpringApplication.run(AgroCalculatorServer.class, args);
    }
}
