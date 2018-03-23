package lerrain.service.product;

import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Map;

@Controller
public class ProductController
{
    @Autowired
    ProductService productSrv;

    @RequestMapping("/reset")
    @ResponseBody
    public String reset()
    {
        return "success";
    }

    @RequestMapping("/save.json")
    @ResponseBody
    public JSONObject list(@RequestBody JSONObject p)
    {
        Log.info(p);

        Long clauseId = p.getLong("id");
        Long companyId = p.getLong("companyId");
        Long categoryId = p.getLong("categoryId");
        String clauseCode = p.getString("code");
        String clauseName = p.getString("name");
        int type = p.getIntValue("type");

        clauseId = productSrv.save(clauseId, clauseCode, clauseName, companyId, type, categoryId);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", clauseId);

        return res;
    }
}
