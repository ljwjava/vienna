package lerrain.service.underwriting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ImportResource;

@EnableAutoConfiguration
@ImportResource(locations={"classpath:spring.xml"})
public class UnderwritingStarter
{
	public static void main(String[] args) throws Exception
	{
		SpringApplication.run(UnderwritingStarter.class, args);
	}
}
