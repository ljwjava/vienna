package lerrain.service.biz;

import lerrain.tool.formula.Factors;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ImportResource;

@EnableAutoConfiguration
@EnableFeignClients
@ImportResource(locations={"classpath:spring.xml"})
public class Athens
{
	public static void main(String[] args) throws Exception
	{
		SpringApplication.run(Athens.class, args);
	}
}
