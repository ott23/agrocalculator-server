package net.tngroup.acserver.node;

import net.tngroup.acserver.nodeserver.NodeServer;
import net.tngroup.acserver.nodeserver.components.TaskComponent;
import net.tngroup.acserver.databases.h2.models.Setting;
import net.tngroup.acserver.databases.h2.services.SettingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@PropertySource("classpath:settings.properties")
public class AutoExecutor implements ApplicationRunner {

    @Value("${settings.list}")
    private String settingsString;

    private Logger logger = LogManager.getFormatterLogger("CommonLogger");

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
        loadDefaultProperties();
        nodeServer.createBootstrap();
        logger.info("Node server started");
        taskComponent.start();
    }

    private void loadDefaultProperties() {
        String[] calculatorProperties = settingsString.split(",");
        Arrays.stream(calculatorProperties).forEach((p) -> {
            Setting setting = settingService.getByNameAndCalculatorId(p.trim(), null);
            if (setting == null) settingService.save(new Setting(p.trim(), env.getProperty(p.trim())));
        });
    }


}
