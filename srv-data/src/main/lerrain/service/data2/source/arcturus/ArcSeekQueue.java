package lerrain.service.data2.source.arcturus;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ArcSeekQueue extends ArcQueue<Long, ArcSeekQueue.FileContent>
{
	@Override
	protected void add(String file, Long id)
	{
		if (id == null)
			System.out.println(file);

		FileContent fc = map.get(file);

		if (fc == null)
		{
			fc = new ArcSeekQueue.FileContent(id);
			map.put(file, fc);
		}
		else
		{
			fc.add(id);
		}
	}

	@Override
	protected void write(String file, FileContent fc)
	{
		fc.write(file);
	}

	public static class FileContent
	{
		List<Long> seeks;

		public FileContent(Long id)
		{
			seeks = new ArrayList<>();

			add(id);
		}

		public void add(Long id)
		{
			seeks.add(id);
		}

		public void write(String file)
		{
			File f = new File(file);
			f.getParentFile().mkdirs();

//			if (!moves.isEmpty())
//			{
//				try (RandomAccessFile os = new RandomAccessFile(f, "rw"))
//				{
//					long len = os.length();
//					for (long i=0;i<len;i+=8)
//					{
//						Long v = os.readLong();
//						if (moves.indexOf(v) >= 0)
//						{
//							os.seek(os.getFilePointer() - 8);
//							os.writeLong(0);
//						}
//					}
//				}
//				catch (Exception e1)
//				{
//					throw new RuntimeException(e1);
//				}
//			}

			try (DataOutputStream os = new DataOutputStream(new FileOutputStream(f, f.exists())))
			{
				for (Long l : seeks)
					os.writeLong(l);
			}
			catch (Exception e1)
			{
				throw new RuntimeException(e1);
			}
		}
	}
}
