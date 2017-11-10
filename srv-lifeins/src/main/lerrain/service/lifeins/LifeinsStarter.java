package lerrain.service.lifeins;

import lerrain.project.insurance.plan.Plan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ImportResource;

@EnableAutoConfiguration
@ImportResource(locations={"classpath:spring.xml"})
public class LifeinsStarter
{
	public static void main(String[] args) throws Exception
	{
		ConfigurableApplicationContext cac = SpringApplication.run(LifeinsStarter.class, args);

//		test((LifeinsService)cac.getBean("lifeins"));
	}

	private static void test(LifeinsService ls)
	{
		Customer app = new Customer();
		app.setAge(25);
		app.setGenderCode(Customer.GENDER_MALE);

		Customer ins = new Customer();
		ins.setAge(1);
		ins.setGenderCode(Customer.GENDER_MALE);

		Plan plan = new Plan(app, ins);
		plan.newCommodity(ls.getProduct("HXH00004"));

		System.out.println(LifeinsShow.formatTable(plan.primaryCommodity(), true));
	}

}
