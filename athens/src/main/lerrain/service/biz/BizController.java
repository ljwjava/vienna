package lerrain.service.biz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.ServiceMgr;
import lerrain.tool.Common;
import lerrain.tool.script.Script;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;

@Controller
public class BizController
{
    @Autowired PlatformService platformSrv;

    private Platform getPlatform(JSONObject json)
    {
        Long platformId = json.getLong("platformId");
        if (platformId != null)
            return platformSrv.getPlatform(platformId);
        else
            return platformSrv.getPlatform(json.getString("channel"));
    }

    @RequestMapping("/replace.json")
    @ResponseBody
    public JSONObject replace(@RequestBody JSONObject req)
    {
        String scriptStr = req.getString("script");
        String funcName = req.getString("name");

        Platform p = getPlatform(req);
        p.putVar(funcName, Script.scriptOf(scriptStr));

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }
}
