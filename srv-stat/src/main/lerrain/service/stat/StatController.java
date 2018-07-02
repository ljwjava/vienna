package lerrain.service.stat;

import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
public class StatController
{
    @Autowired
    StatService statSrv;

    @RequestMapping({ "/act.json" })
    @ResponseBody
    public JSONObject act(@RequestBody JSONObject json)
    {
        Log.stat(json.toJSONString());

        Long userId = json.getLong("userId");
        Long platformId = json.getLong("platformId");
        String action = json.getString("action");

        statSrv.count(new Date(), platformId, userId, action);

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }
}
