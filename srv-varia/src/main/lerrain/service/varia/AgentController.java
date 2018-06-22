package lerrain.service.varia;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AgentController
{
    @Autowired
    AgentFetchService agentFetchSrv;

    @RequestMapping({ "/verify.json" })
    @ResponseBody
    public JSONObject verify(@RequestBody JSONObject json)
    {
        String code = json.getString("certCode");
        String name = json.getString("name");
        String bizCode = json.getString("bizCode");
//        String certfiCode = json.getString("certfiCode");

        code = code.substring(code.length() - 4).toUpperCase();

        if (!Common.isEmpty(bizCode))
            bizCode = bizCode.substring(bizCode.length() - 4).toUpperCase();

//        if (!Common.isEmpty(certfiCode))
//            certfiCode = certfiCode.substring(certfiCode.length() - 4).toUpperCase();

        List<Map> j1 = agentFetchSrv.find1(code, name, bizCode);
        List<Map> j2 = agentFetchSrv.find2(code, name, bizCode);

        JSONObject res = new JSONObject();
        if ((j1 == null || j1.isEmpty()) && (j2 == null || j2.isEmpty()))
        {
            res.put("result", "fail");
            res.put("reason", "未找到代理人信息");
        }
        else
        {
            Map m = null;
            if (j1 != null && j1.size() > 1)
            {
                m = j1.get(0);
                m.put("more", true);
            }
            else if (j2 != null && j2.size() > 1)
            {
                m = j2.get(0);
                m.put("more", true);
            }

            res.put("result", "success");
            res.put("content", m);
        }

        return res;
    }

    @RequestMapping({ "/test" })
    @ResponseBody
    public String test(@RequestParam("code") String code, @RequestParam("name") String name) throws Exception
    {
        List m1 = agentFetchSrv.find1(code, name, null);
        List m2 = agentFetchSrv.find2(code, name, null);

        System.out.println(m1);
        System.out.println(m2);

        return "succ";
    }

    Map temp = new HashMap();

    @RequestMapping({ "/fetch.json" })
    @ResponseBody
    public JSONObject fetch(@RequestBody JSONObject json)
    {
        String code = json.getString("certCode");
        String name = json.getString("name");

        JSONObject res = new JSONObject();
        res.put("result", "success");

        synchronized (temp)
        {
            if (temp.containsKey(code + "/" + name))
            {
                System.out.println(code + "/" + name + " checked skip...");
                return res;
            }

            temp.put(code + "/" + name, 1);
        }

        agentFetchSrv.find1(code, name, null);
        agentFetchSrv.find2(code, name, null);

        return res;
    }
}
