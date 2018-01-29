package lerrain.service.data2;

import lerrain.tool.script.Stack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DataService
{
	@Autowired DataDao dataDao;

	Map<Object, Stack> envMap;

	public void reset()
	{
		envMap = dataDao.loadAllEnv();
	}

	public Map<Object, Stack> getEnvMap()
	{
		return envMap;
	}

	public Stack getEnv(Long envId)
	{
		return envMap.get(envId);
	}

	public Stack getEnv(String envCode)
	{
		return envMap.get(envCode);
	}
}
