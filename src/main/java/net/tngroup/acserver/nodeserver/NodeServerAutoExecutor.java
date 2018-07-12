package net.tngroup.acserver.nodeserver;

import net.tngroup.acserver.nodeserver.components.TaskComponent;
import net.tngroup.acserver.databases.h2.models.Setting;
import net.tngroup.acserver.databases.h2.services.SettingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class NodeServerAutoExecutor implements ApplicationRunner {

    private Logger logger = LogManager.getFormatterLogger("ConsoleLogger");

    private NodeServer nodeServer;
    private Environment env;
    private SettingService settingService;
    private TaskComponent taskComponent;

    @Autowired
    public NodeServerAutoExecutor(NodeServer nodeServer,
                                  Environment env,
                                  SettingService settingService,
                                  TaskComponent taskComponent) {
        this.nodeServer = nodeServer;
        this.env = env;
        this.settingService = settingService;
        this.taskComponent = taskComponent;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        loadProperties();

        nodeServer.createBootstrap();
        logger.info("Node server started");

        taskComponent.start();
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
            if (setting == null) settingService.save(new Setting(p, env.getProperty("default." + p)));
        });
    }


}
