package lerrain.project.vienna;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

import java.io.File;

@EnableAutoConfiguration
@EnableFeignClients
@ImportResource("spring.xml")
public class Vienna
{
    public static void main(String[] args) throws Exception
    {
        SpringApplication.run(Vienna.class, args);
    }
}
