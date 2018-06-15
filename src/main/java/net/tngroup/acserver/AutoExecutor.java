package net.tngroup.acserver;

import net.tngroup.acserver.services.NodeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AutoExecutor implements ApplicationRunner {

    private Logger logger = LogManager.getFormatterLogger("ConsoleLogger");

    private NodeService nodeService;

    @Autowired
    public AutoExecutor(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        logger.info("Auto executor started");

        new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    nodeService.handleTasks();
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}
