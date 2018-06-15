package lerrain.service.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Role implements Serializable
{
	Long id;

	String code;
	String xxx;
	String name;

	int rank;

	public int getRank()
	{
		return rank;
	}

	public void setRank(int rank)
	{
		this.rank = rank;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}
}
