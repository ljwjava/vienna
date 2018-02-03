package lerrain.service.env.function;

import lerrain.service.common.Log;
import lerrain.tool.CipherUtil;
import lerrain.tool.Common;
import lerrain.tool.Disk;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileFX implements Factors
{
    Function base64;

    public FileFX()
    {
        base64 = new Function()
        {
            @Override
            public Object run(Object[] objects, Factors factors)
            {
                File file = new File((String)objects[0]);
                return Common.encodeBase64(Disk.load(file));
            }
        };
    }

    @Override
    public Object get(String s)
    {
        if ("base64".endsWith(s))
            return base64;

        return null;
    }
}
