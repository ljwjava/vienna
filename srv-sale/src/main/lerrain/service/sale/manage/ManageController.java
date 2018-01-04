package lerrain.service.sale.manage;

import com.alibaba.fastjson.JSONObject;
import lerrain.service.sale.Ware;
import lerrain.service.sale.WareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

public class ManageController
{
    @Autowired
    WareService wareSrv;

    @RequestMapping("/config.json")
    @ResponseBody
    public JSONObject config(@RequestBody JSONObject req)
    {
        Ware ware = wareSrv.getWare(req.getLong("wareId"));



        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", ware);

        return res;
    }
}
