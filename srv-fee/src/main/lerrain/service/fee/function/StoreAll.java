package lerrain.service.fee.function;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.fee.Fee;
import lerrain.service.fee.FeeService;
import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StoreAll implements Function
{
    @Autowired
    FeeService feeSrv;

    @Override
    public Object run(Object[] objects, Factors factors)
    {
        JSONArray list = (JSONArray)JSON.toJSON(objects[0]);
        for (int i = 0; i < list.size(); i++)
        {
            JSONObject l = list.getJSONObject(i);

            Fee r = new Fee();
            r.setPlatformId(Common.toLong(factors.get("PLATFORM_ID")));
            r.setDraweeType(Common.intOf(objects[i++], 0));
            r.setDrawee(Common.toLong(objects[i++]));
            r.setPayeeType(Common.intOf(objects[i++], 0));
            r.setPayee(Common.toLong(objects[i++]));

            r.setProductId(objects[i++].toString());
            r.setBizNo(Common.trimStringOf(objects[i++]));
            r.setAmount(Common.doubleOf(objects[i++], 0));
            r.setAuto(Common.boolOf(objects[i++], false));
            r.setEstimate(Common.dateOf(objects[i++]));
            r.setType(Common.intOf(objects[i++], 0));
            r.setUnit(Common.intOf(objects[i++], 1));
            r.setFreeze(Common.intOf(objects[i++], 1));
            r.setExtra((Map)objects[i++]);
            r.setMemo(Common.trimStringOf(objects[i++]));

            if (r.getAmount() > 0 && r.getPayee() != null)
                feeSrv.store(r);
        }

        return null;
    }
}
