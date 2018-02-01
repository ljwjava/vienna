package lerrain.service.core;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PolicyController
{
    @Autowired
    PolicyService policySrv;

    @RequestMapping("/upload/excel.json")
    @ResponseBody
    public JSONObject uploadExcel()
    {
        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }
}
