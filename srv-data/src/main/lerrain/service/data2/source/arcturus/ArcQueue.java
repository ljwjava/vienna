package lerrain.service.data2.source.arcturus;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class ArcQueue<T1, T2>
{
	public static boolean play = true;

	int temp = 0;

	Map<String, T2> map = new LinkedHashMap<>();
	Map<String, T2> pack = new HashMap<>();

	public void start()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				write();
			}
		}).start();
	}

	public void stop()
	{
		play = false;

		synchronized (map)
		{
			map.notify();
		}
	}

	protected abstract void add(String file, T1 id);

	protected abstract void write(String file, T2 val);

	public void push(String file, T1 id)
	{
		synchronized (map)
		{
			add(file, id);
			map.notify();
		}
	}

	public int getQueueSize()
	{
		return map.size() + pack.size() - temp;
	}

	public void write()
	{
		while (play || !map.isEmpty())
		{
			synchronized (map)
			{
				pack.putAll(map);
				map.clear();
			}

			for (Map.Entry<String, T2> e : pack.entrySet())
			{
				write(e.getKey(), e.getValue());
				temp++;
			}

			synchronized (pack)
			{
				pack.clear();
			}

			temp = 0;

			try
			{
				synchronized (map)
				{
					if (map.isEmpty() && play)
						map.wait();
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
