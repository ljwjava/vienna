package lerrain.service.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Excel
{
    public static String[][] TITLE = new String[][] {
            {"险种类别", "biz_type"},
            {"保单类型", "option"},
            {"业务员名称", "owner"},
            {"保单号", "policy_no"},
            {"保险公司", "company_name"},
            {"险种", "ins_type"},
            {"客户名称", "applicant_name"},
            {"车牌", "vehicle_plate_no"},
            {"投保时间", "apply_time", "time"},
            {"保险开始时间", "effective_time", "time"},
            {"保险结束时间", "end_time", "time"},
            {"总保费", "premium", "number"},
            {"代理费率", "fee_rate", "number"},
            {"代理费", "fee", "number"},
            {"业务佣金率", "cms_rate", "number"},
            {"业务员佣金", "cms", "number"},
            {"业务来源", "source"},
            {"项目名称", "project_name"},
            {"出险次数", "happen"},
            {"保险次数", "claim"},
            {"保额", "amount", "number"},
            {"登记日期", "register_date", "date"},
            {"抢收奖励", "bonus", "number"},
            {"导入时间", "create_time", "time"}
    };

    public static Map<String, JSONObject> DICT = new HashMap();

    static
    {
        DICT.put("biz_type", JSON.parseObject("{'车险':'1'}"));
        DICT.put("ins_type", JSON.parseObject("{'交强险':'1', '商业险':'2'}"));
        DICT.put("option", JSON.parseObject("{'新单':'1', '批改':'2'}"));
        DICT.put("company_name", JSON.parseObject("{'人保财险':'18', '平安财险':'9', '大地财险':'28', '国寿财险':'29', '太平洋财险':'30', '信达财险':'31', '长安财险':'32', '中华联合':'33'}"));
    }

}
