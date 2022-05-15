package vmn.application.Main;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
public class MappingApi {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MappingApi.class);

    @RequestMapping(value = "/test")

    @ResponseBody
    String handleRequest() {
        logger.info("Received Request!!");
        //logger.info("param: " + str);
        return "Hello World!";
    }
}
