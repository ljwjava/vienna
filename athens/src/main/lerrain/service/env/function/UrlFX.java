package lerrain.service.env.function;

import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
        // link转map，并将value解码
        this.put("paramOfDE", new Function() {
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

                try {
                    String[] kvs = url.split("&");
                    for (String kv : kvs) {
                        if(kv.indexOf("=") >= 0){
                            String[] kkvv = kv.split("=");
                            jo.put(kkvv[0], URLDecoder.decode(kkvv[1], "utf-8"));
                        }
                    }
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }

                return jo;
            }
        });
        // map转link，并将value编码
        this.put("linkOfEN", new Function() {
            @Override
            public Object run(Object[] objects, Factors factors) {
                Map<String, Object> map = (Map<String, Object>)objects[0];
                if (map == null)
                    return null;

                String url = null;
                try {
                    for (Map.Entry e : map.entrySet()) {
                            url = (url == null ? "" : url + "&") + e.getKey() + "=" + URLEncoder.encode(String.valueOf(e.getValue()),"utf-8");
                    }
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }

                return url;
            }
        });
        // 编码
        this.put("encode", new Function() {
            @Override
            public Object run(Object[] v, Factors factors) {
                String str = String.valueOf(v[0]);
                if (Common.isEmpty(str))
                    return null;

                try {
                     return URLEncoder.encode(str, "utf-8");
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                return null;
            }
        });
        // 解码
        this.put("decode", new Function() {
            @Override
            public Object run(Object[] v, Factors factors) {
                String str = String.valueOf(v[0]);
                if (Common.isEmpty(str))
                    return null;

                try {
                     return URLDecoder.decode(str, "utf-8");
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                return null;
            }
        });
    }

}
