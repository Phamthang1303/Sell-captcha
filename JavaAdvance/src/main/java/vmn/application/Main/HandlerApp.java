package vmn.application.Main;

import java.io.File;
import java.util.logging.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.LoggerFactory;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;

//@EnableAutoConfiguration
@SpringBootApplication
public class HandlerApp {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(String.valueOf(HandlerApp.class));

    public static void main(String[] args) {
        //BasicConfigurator.configure();
        String log4jConfigFile = System.getProperty("user.dir")
                + File.separator + "src/main/resources/log4j.properties";
        PropertyConfigurator.configure(log4jConfigFile);
        SpringApplication.run(HandlerApp.class, args);
        logger.info("Porting at 80...");
    }
}
