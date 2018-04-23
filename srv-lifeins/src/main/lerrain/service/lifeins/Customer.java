package lerrain.service.lifeins;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lerrain.project.insurance.plan.InsuranceCustomer;
import lerrain.service.common.Log;
import lerrain.tool.Common;

public class Customer implements InsuranceCustomer
{
	public static final int GENDER_MALE			= 1;
	public static final int GENDER_FEMALE		= 2;
	public static final int GENDER_UNKNOW		= 3;
	
	String id;
	String name;

	int genderCode = GENDER_MALE;
	
	Date birthday;

	String occupationCode;

	int age = -1, day = -1;
	
	Map<String, Object> vals = new HashMap<>();

	public Customer()
	{
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public boolean equals(Object val)
	{
		if (val == null)
			return false;

		if (val == this)
			return true;

		if (!(val instanceof Customer))
			return false;

		if (this.id == null)
			return false;

		return this.id.equals(((Customer)val).id);
	}

	public Customer(Date birthday)
	{
		this.birthday = birthday;
	}
	
	public int getAge()
	{
		if (birthday == null)
			return -1;

		if (age < 0)
			age = Common.getAge(birthday);

		return age;
	}

	public void setAge(int age)
	{
		setBirthday(new Date(new Date().getTime() - 3600000L * 24 * (365 * age + 180)));
	}

	public int getDayCount()
	{
		if (birthday == null)
			return -1;
		
		if (day < 0)
			day = (int)((new Date().getTime() - birthday.getTime()) / 1000 / 3600 / 24);
		
		return day;
	}

	public Date getBirthday()
	{
		return birthday;
	}

	public void setBirthday(Date birthday)
	{
		age = -1;
		
		this.birthday = birthday;
	}

	public int getGenderCode()
	{
		return genderCode;
	}

	public void setGenderCode(int genderCode)
	{
		this.genderCode = genderCode;
	}

	public Object get(String name) 
	{
		if ("OCCUPATION_CODE".equals(name))
			return occupationCode;
		if ("AGE".equals(name))
			return getAge();
		if ("GENDER".equals(name))
			return genderCode;

		return vals.get(name);
	}
	
	public int getOccupationCategory(String typeCode)
	{
		return 1;
	}

	public String getId()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public void set(String key, Object val)
	{
		vals.put(key, val);
	}
}
