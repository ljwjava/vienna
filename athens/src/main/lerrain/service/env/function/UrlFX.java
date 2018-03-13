package lerrain.service.env.function;

import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by dingliang on 2018/3/12.
 */
@Service
public class UrlFX extends HashMap<String, Object>
{
    public UrlFX() {
        this.put("paramOf", new Function() {
            @Override
            public Object run(Object[] v, Factors factors) {
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

                url = URLEncoder.encode(url);

                String[] kvs = url.split("&");
                for (String kv : kvs) {
                    if(kv.indexOf("=") >= 0){
                        String[] kkvv = kv.split("=");
                        jo.put(kkvv[0], kkvv[1]);
                    }
                }

                return jo;
            }
        });
        this.put("linkOf", new Function() {
            @Override
            public Object run(Object[] objects, Factors factors) {
                Map<String, Object> map = (Map<String, Object>)objects[0];
                if (map == null)
                    return null;

                String url = null;
                for (Map.Entry e : map.entrySet())
                    url = (url == null ? "" : url + "&") + e.getKey() + "=" + e.getValue();

                return url;
            }
        });
    }
}
