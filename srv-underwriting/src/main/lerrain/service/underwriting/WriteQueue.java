package lerrain.service.underwriting;

import lerrain.service.common.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WriteQueue implements Runnable
{
	public static final int MAX	= 100000;

	@Autowired
	UnderwritingDao planDao;

	Map<Long, Underwriting> list = new HashMap<>();

	Thread thread = new Thread(this);

	public void add(Underwriting msg)
	{
		if (msg == null || msg.getId() == null)
			return;

		synchronized (list)
		{
			if (list.size() < MAX)
				list.put(msg.getId(), msg);
			
			list.notify();
		}
	}

	public void start()
	{
		if (!thread.isAlive())
			thread.start();
	}

	public void run()
	{
		Map<Long, Underwriting> pack = new HashMap<>();
		
		while (true)
		{
			synchronized (list)
			{
				pack.putAll(list);
				list.clear();
			}
			
			if (pack.size() > 0)
			{
				for (Underwriting uw : pack.values())
				{
					try
					{
						planDao.save(uw);
					}
					catch (Exception e)
					{
						Log.alert(e);
					}
				}

				try
				{
					Thread.sleep(10000);
				}
				catch (InterruptedException e)
				{
				}
			}
			else synchronized (list) 
			{ 
				try
				{
					if (list.isEmpty())
						list.wait();
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
	
			pack.clear();
		}
	}
}
