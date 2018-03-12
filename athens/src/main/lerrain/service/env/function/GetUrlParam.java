package lerrain.service.env.function;

import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.stereotype.Service;


/**
 * Created by dingliang on 2018/3/12.
 */
@Service
public class GetUrlParam implements Function
{
    @Override
    public Object run(Object[] v, Factors factors)
    {
        String url = String.valueOf(v[0]);
        if (Common.isEmpty(url))
            return null;

        JSONObject jo = new JSONObject();
        if(url.indexOf("?") >= 0){
            url = url.substring(url.indexOf("?")+1);
        }
        if(Common.isEmpty(url)){
            return jo;
        }

        String[] kvs = url.split("&");
        for (String kv : kvs) {
            if(kv.indexOf("=") >= 0){
                String[] kkvv = kv.split("=");
                jo.put(kkvv[0], kkvv[1]);
            }
        }

        return jo;
    }

}
