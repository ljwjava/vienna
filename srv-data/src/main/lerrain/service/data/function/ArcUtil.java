package lerrain.service.data.function;

import lerrain.service.data.source.arcturus.ArcDoc;
import lerrain.service.data.source.arcturus.ArcMap;
import lerrain.service.data.source.arcturus.ArcTool;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lerrain on 2017/11/4.
 */
public class ArcUtil extends HashMap<String, Object>
{
    public ArcUtil()
    {
        this.put("find", new Function()
        {
            @Override
            public Object run(Object[] objects, Factors factors)
            {
                return ArcTool.find((ArcMap) objects[0], objects[1].toString(), objects[2].toString());
            }
        });

        this.put("save", new Function()
        {
            @Override
            public Object run(Object[] objects, Factors factors)
            {
                return ArcTool.save((ArcDoc) objects[0], objects[1].toString(), objects[2].toString());
            }
        });
    }
}