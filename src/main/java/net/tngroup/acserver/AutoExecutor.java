package net.tngroup.acserver;

import net.tngroup.acserver.components.NodeComponent;
import net.tngroup.acserver.components.server.NodeServer;
import net.tngroup.acserver.models.Setting;
import net.tngroup.acserver.services.SettingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class AutoExecutor implements ApplicationRunner {

    private Logger logger = LogManager.getFormatterLogger("ConsoleLogger");

    private NodeServer nodeServer;
    private NodeComponent nodeComponent;
    private Environment env;
    private SettingService settingService;

    @Autowired
    public AutoExecutor(NodeServer nodeServer,
                        NodeComponent nodeComponent,
                        Environment env,
                        SettingService settingService) {
        this.nodeServer = nodeServer;
        this.nodeComponent = nodeComponent;
        this.env = env;
        this.settingService = settingService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        loadProperties();
        taskHandlerInit();

        nodeServer.createBootstrap();
        logger.info("Server started");

    }

    private void loadProperties() {
        String[] calculatorProperties = {
                "kafka.bootstrap-servers",
                "kafka.group-id",
                "kafka.poll-timeout",
                "kafka.send-timeout",
                "kafka.test-timeout",
                "kafka.tasks-topics",
                "kafka.results-topic-prefix",
                "pool.max-count",
                "pool.timeout",
                "rest.url"
        };

        Arrays.stream(calculatorProperties).forEach((p) -> {
            Setting setting = settingService.getByNameAndCalculatorId(p, null);
            if (setting == null) settingService.add(new Setting(p, env.getProperty("default." + p)));
        });
    }

    private void taskHandlerInit() {
        new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    nodeComponent.handleTasks();
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}
