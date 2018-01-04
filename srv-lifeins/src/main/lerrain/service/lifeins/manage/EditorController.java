package lerrain.service.lifeins.manage;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class EditorController
{
    @Autowired
    EditorService editorSrv;

    @RequestMapping("edit/load.json")
    @ResponseBody
    public JSONObject load(@RequestBody JSONObject req)
    {
        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", editorSrv.load(req.getString("productId")).toJson());

        return res;
    }

    @RequestMapping("edit/preview.json")
    @ResponseBody
    public JSONObject preview(@RequestBody JSONObject req)
    {
        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", editorSrv.load(req.getString("productId")).toString());

        return res;
    }
}
