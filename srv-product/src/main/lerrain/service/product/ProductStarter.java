package lerrain.service.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ImportResource;

@EnableAutoConfiguration
@ImportResource(locations={"classpath:spring.xml"})
public class ProductStarter
{
	public static void main(String[] args) throws Exception
	{
		SpringApplication.run(ProductStarter.class, args);
	}
}
