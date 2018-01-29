package lerrain.service.data2.source.arcturus;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class ArcTextQueue extends ArcQueue<byte[], byte[]>
{
	@Override
	public void add(String file, byte[] b)
	{
		map.put(file, b);
	}

	@Override
	protected void write(String file, byte[] val)
	{
		File f = new File(file);
		f.getParentFile().mkdirs();

		try (DataOutputStream os = new DataOutputStream(new FileOutputStream(f)))
		{
			os.write(val);
		}
		catch (Exception e1)
		{
			throw new RuntimeException(e1);
		}
	}
}
