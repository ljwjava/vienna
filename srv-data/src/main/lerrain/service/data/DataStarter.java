package lerrain.service.data;

import lerrain.service.common.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ImportResource;

@EnableAutoConfiguration
@ImportResource(locations={"classpath:spring.xml"})
public class DataStarter
{
	public static void main(String[] args) throws Exception
	{
		ConfigurableApplicationContext wac = SpringApplication.run(DataStarter.class, args);
	}
}
