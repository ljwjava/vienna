package lerrain.service.proposal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ImportResource;

@EnableAutoConfiguration
@EnableFeignClients
@ImportResource(locations={"classpath:spring.xml"})
public class ProposalStarter
{
	public static void main(String[] args) throws Exception
	{
		SpringApplication.run(ProposalStarter.class, args);
	}
}
