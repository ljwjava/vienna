package lerrain.service.template;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ImportResource;

/**
 * @author yangsheng
 * @date 2018/5/31
 */
@EnableAutoConfiguration
@ImportResource(locations={"classpath:spring.xml"})
public class TemplateStarter {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(TemplateStarter.class, args);
    }

}

