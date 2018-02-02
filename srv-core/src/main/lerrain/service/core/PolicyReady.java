package lerrain.service.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;

import java.util.HashMap;
import java.util.Map;

public class PolicyReady extends Policy
{
    int result;
    String memo;

    Policy policy;

    public PolicyReady(Map<String, Object> val)
    {
        super(val);

        result = Common.intOf(val.get("result"), -1);
    }

    public String verify()
    {
        String policyNo = getString("policy_no");
        if (Common.isEmpty(policyNo))
            return "保单号为空";

        return null;
    }

    public void prepare()
    {
        String policyNo = this.getString("policy_no");
        if (Common.isEmpty(policyNo))
            throw new RuntimeException("保单号为空");

        String[] keys = new String[] {"option", "biz_type", "ins_type", "company_name|company_id"};
        for (String key : keys)
        {
            String destKey = key;
            if (key.indexOf("|") > 0)
            {
                String[] str = key.split("[|]");
                key = str[0];
                destKey = str[1];
            }

            String s = Common.trimStringOf(this.getString(key));
            JSONObject d = Excel.DICT.get(key);
            boolean find = false;
            for (String str : d.keySet())
            {
                if (("/" + str + "/").indexOf("/" + s + "/") >= 0)
                {
                    find = true;
                    put(destKey, d.getInteger(str));
                    break;
                }
            }

            if (!find)
                throw new RuntimeException(key + "无法匹配");
        }

        int mode = Common.intOf(val.get("mode"), 0);
        if (mode == 2)
        {
            int pos = policyNo.indexOf("-批改");
            if (pos > 0)
            {
                String p1 = policyNo.substring(0, pos);
                String p2 = policyNo.substring(pos + 3);

                val.put("policy_no", p1);
                val.put("endorse", Common.intOf(p2, 1));
            }
            else
            {
                throw new RuntimeException("类型为批改，但保单号中未标记批改及批改次数");
            }
        }
        else if (mode == 1)
        {
            int pos = policyNo.indexOf("-");
            if (pos >= 0)
                throw new RuntimeException("类型为新单，但保单号中有特殊标记");
        }
        else
        {
            throw new RuntimeException("类型为无法识别");
        }
    }

    public boolean compare(Policy policy)
    {
        this.policy = policy == null ? new Policy(val) : policy;

        if (policy == null)
        {
            this.policy = new Policy(val);
        }
        else
        {
            this.policy = policy;

        }

        result = 1;
        memo = null;

        return false;
    }
}
