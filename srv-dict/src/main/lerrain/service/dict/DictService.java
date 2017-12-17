package lerrain.service.dict;

import lerrain.service.dict.ip.DipSeeker;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;


@Service
public class DictService
{
	@Autowired
	DictDao dictDao;

	Map<String, Object> dicts = new HashMap<>();

	public void reset()
	{
		synchronized (dicts)
		{
			dicts.clear();
		}
	}

	public Object getDict(String company, String name, String version)
	{
		String key = (company != null ? company + "/" + name : name) + "/" + version;

		synchronized (dicts)
		{
			if (!dicts.containsKey(key))
			{
				Object dict = dictDao.load(company, name, version);
				if (dict == null)
					dict = dictDao.load(name, version);

				dicts.put(key, dict);
			}
		}

		return dicts.get(key);
	}
}
