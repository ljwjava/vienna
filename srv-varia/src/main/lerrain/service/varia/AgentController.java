package lerrain.service.varia;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class AgentController
{
    @Autowired
    AgentService agentSrv;

    @RequestMapping({ "/verify.json" })
    @ResponseBody
    public JSONObject verify(@RequestBody JSONObject json)
    {
        String code = json.getString("certCode");
        String name = json.getString("name");

        Map m = agentSrv.find(code, name);

        JSONObject res = new JSONObject();
        if (m == null || m.isEmpty())
        {
            res.put("result", "fail");
            res.put("reason", "未找到代理人信息");
        }
        else
        {
            m.remove("file");
            m.remove("file2");

            res.put("result", "success");
            res.put("content", m);
        }

        return res;
    }

    @RequestMapping({ "/test" })
    @ResponseBody
    public String test(@RequestParam("code") String code, @RequestParam("name") String name)
    {
        Map m = agentSrv.find(code, name);
        return "<pre>" + JSON.toJSONString(m) + "</pre>";
    }
}
