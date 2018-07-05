package lerrain.service.stat;

import lerrain.service.common.Log;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class StatService
{
	@Autowired
	StatDao statDao;

	Map<String, Map<String, Integer>> temp = new LinkedHashMap<>();

	public void count(Date time, Long platformId, Long userId, String action)
	{
		String dateStr = Common.getString(time);

		synchronized (temp)
		{
			Map map = temp.get(dateStr);
			if (map == null)
			{
				map = new HashMap();
				temp.put(dateStr, map);
			}

			int count = Common.intOf(map.get(platformId + " / " + userId + " / " + action), 0);
			count++;

			map.put(platformId + " / " + userId + " / " + action, count);
		}
	}

	@PostConstruct
	public void start()
	{
		Thread th = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				while (true)
				{
					Map<String, Integer> map = new HashMap<>();

					synchronized (temp)
					{
						temp.forEach((k1, v1) -> {
							v1.forEach((k2, v2) -> {
								map.put(k1 + " / " + k2, v2);
							});
						});

						if (temp.size() > 2)
						{
							String day = temp.keySet().iterator().next();
							temp.remove(day);
						}
					}

					try
					{
						statDao.save(map);
					}
					catch (Exception e)
					{
						Log.error(e);
					}

					try
					{
						Thread.sleep(10000L);
					}
					catch (InterruptedException e)
					{
					}
				}
			}
		});

		th.start();
	}
}
