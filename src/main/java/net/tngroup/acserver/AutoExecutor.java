package net.tngroup.acserver;

import net.tngroup.acserver.components.TaskComponent;
import net.tngroup.acserver.server.NodeServer;
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
    private Environment env;
    private SettingService settingService;
    private TaskComponent taskComponent;

    @Autowired
    public AutoExecutor(NodeServer nodeServer,
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
        logger.info("Server started");

        taskComponent.init();

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
            if (setting == null) settingService.addOrUpdate(new Setting(p, env.getProperty("default." + p)));
        });
    }


}
