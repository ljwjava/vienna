package lerrain.service.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ImportResource;

@EnableAutoConfiguration
@ImportResource(locations={"classpath:spring.xml"})
public class OrderStarter
{
	public static void main(String[] args) throws Exception
	{
		SpringApplication.run(OrderStarter.class, args);
	}
}
