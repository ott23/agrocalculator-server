package net.tngroup.acserver;

import net.tngroup.acserver.models.Property;
import net.tngroup.acserver.repositories.PropertyRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class AutoPropertyInitializer implements ApplicationRunner {

    private Logger logger = LogManager.getFormatterLogger("ConsoleLogger");

    private Environment env;
    private PropertyRepository propertyRepository;

    private String[] calculatorProperties = {
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

    @Autowired
    public AutoPropertyInitializer(Environment env, PropertyRepository propertyRepository) {
        this.env = env;
        this.propertyRepository = propertyRepository;
    }

    @Override
    public void run(ApplicationArguments args) {

        logger.info("Auto property initializer started");

        Arrays.stream(calculatorProperties).forEach((p) -> {
            Property property = propertyRepository.findPropertyByNameAndCalculator(p, null);
            if (property == null) {
                String propertyValue = env.getProperty("default." + p);
                property = new Property(p, propertyValue);
                propertyRepository.save(property);
            }
        });
    }
}
