package lerrain.service.product.fee;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class CustFeeController
{
    @Autowired
    CustFeeService ccs;

    @RequestMapping("/fee/rate/query.json")
    @ResponseBody
    public JSONObject listFeeDef(@RequestBody JSONObject c)
    {
        Long schemeId = c.getLong("schemeId");
        Long productId = c.getLong("productId");

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", ccs.listFeeDefine(schemeId, productId));

        return res;
    }

    @RequestMapping("/fee/rate/keys.json")
    @ResponseBody
    public JSONObject keys(@RequestBody JSONObject c)
    {
        Long productId = c.getLong("productId");

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", ccs.listFeeDefineKeys(productId));

        return res;
    }

    /**
     * @param c = {
     *     schemeId: Long
     *     productId: Long
     *     content: {
     *         factors: {
     *             ...
     *         }
     *         rate: [ ... ],
     *         begin: Time,
     *         end: Time,
     *         freeze: Integer,
     *         unit: 1-比例 2-固定数额 3-百分比
     *     }
     * }
     * @return
     */
    @RequestMapping("/fee/rate/save.json")
    @ResponseBody
    public JSONObject save(@RequestBody JSONObject c)
    {
        Long schemeId = c.getLong("schemeId");
        saveFeeDefine(schemeId, c);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", schemeId);

        return res;
    }

    /**
     * @param cc = {
     *     schemeId: Long
     *     list: [{
     *         productId: Long
     *         content: {
     *             factors: {
     *                 ...
     *             }
     *             rate: [ ... ],
     *             begin: Time,
     *             end: Time,
     *             freeze: Integer,
     *             unit: 1-比例 2-固定数额 3-百分比
     *         }
     *     }, ...]
     * }
     * @return
     */
    @RequestMapping("/fee/rate/save_all.json")
    @ResponseBody
    public JSONObject saveAll(@RequestBody JSONObject cc)
    {
        Long schemeId = cc.getLong("schemeId");
        JSONArray all = cc.getJSONArray("list");

        for (int i = 0; i < all.size(); i++)
            saveFeeDefine(schemeId, all.getJSONObject(i));

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", schemeId);

        return res;
    }

    private void saveFeeDefine(Long schemeId, JSONObject c)
    {
        Log.info(c);

        Long productId = c.getLong("productId");

        List<CustFeeDefine> list = new ArrayList<>();
        JSONArray content = c.getJSONArray("content");

        for (int i = 0; i < content.size(); i++)
        {
            CustFeeDefine fd = content.getObject(i, CustFeeDefine.class);
            fd.setProductId(productId);
            fd.setSchemeId(schemeId);
            list.add(fd);
        }

        ccs.saveFeeDefine(schemeId, productId, list);
    }
}
