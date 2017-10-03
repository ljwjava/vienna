package lerrain.service.dict;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DictController
{
    @Autowired
    DictService dictSrv;

    @RequestMapping("/health")
    @ResponseBody
    @CrossOrigin
    public String health()
    {
        return "success";
    }

    @RequestMapping("/view.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject dict(@RequestBody JSONObject p)
    {
        String company = p.getString("company");
        String[] name = p.getString("name").split(",");

        JSONObject dicts = new JSONObject();
        for (String n : name)
            dicts.put(n, dictSrv.getDict(company, n));

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", dicts);

        return res;
    }
}
