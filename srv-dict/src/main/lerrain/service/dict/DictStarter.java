package lerrain.service.dict;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ImportResource;

@EnableAutoConfiguration
@ImportResource(locations={"classpath:spring.xml"})
public class DictStarter
{
	public static void main(String[] args) throws Exception
	{
		SpringApplication.run(lerrain.service.dict.DictStarter.class, args);
	}
}
