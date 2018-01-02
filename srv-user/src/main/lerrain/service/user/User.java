package lerrain.service.user;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class User
{
	private int status = Constant.STATUS_TOURIST;

	private Long id;

	private String password;

	private String target;

	private String name;
	private Date loginTime;

	private List<Role> role;

	private Map<String, Object> extra;

	public Long getId()
	{
		return this.id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public List<Role> getRole()
	{
		return role;
	}

	public void setRole(List<Role> role)
	{
		this.role = role;
	}

	public String getTarget()
	{
		return target;
	}

	public void setTarget(String target)
	{
		this.target = target;
	}

	public String getPassword()
	{
		return this.password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Date getLoginTime()
	{
		return loginTime;
	}

	public void setLoginTime(Date loginTime)
	{
		this.loginTime = loginTime;
	}

	public int getStatus()
	{
		return this.status;
	}

	public void setStatus(int status)
	{
		this.status = status;
	}

	public Map<String, Object> getExtra()
	{
		return extra;
	}

	public void setExtra(Map<String, Object> extra)
	{
		this.extra = extra;
	}
}
