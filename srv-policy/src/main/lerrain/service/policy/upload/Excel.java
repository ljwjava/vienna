package lerrain.service.policy.upload;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;
import org.apache.poi.ss.usermodel.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Excel
{
    public static String[][] TITLE = new String[][] {
            {"*险种类别", "biz_type"},
            {"*操作", "operate"},
            {"*业务员", "agent_name"},
            {"*保单号", "policy_no"},
            {"批改单号", "endorse_no"},
            {"批改时间", "endorse_time", "time"},
            {"*保险公司", "company_name"},
            {"*险种", "ins_type"},
            {"*被保险人", "applicant_name"},
            {"车牌号", "vehicle_plate_no"},
            {"*投保时间", "insure_time", "time"},
            {"保险起期", "effective_time", "time"},
            {"保险止期", "finish_time", "time"},
            {"*保费", "premium", "number"},
            {"*代理费率", "fee_rate", "number"},
            {"代理费", "fee", "number"},
            {"*业务佣金率", "cms_rate", "number"},
            {"佣金", "cms", "number"},
            {"业务来源", "source"},
            {"项目名称", "project_name"},
            {"出险次数", "happen"},
            {"保险次数", "claim"},
            {"保额", "amount", "number"},
            {"登记日期", "register_time", "time"},
            {"抢收奖励", "bonus", "number"},
    };

    public static Map<String, String> MAPPING = new HashMap<>();
    public static Map<String, String> ENDORSE = new HashMap<>();

    public static Map<String, JSONObject> DICT = new HashMap();

    static
    {
        MAPPING.put("company_name", "company_id");
        MAPPING.put("operate", "*");
        MAPPING.put("endorse_no", "*");
        MAPPING.put("endorse_time", "*");
        MAPPING.put("ins_type", "type");
        MAPPING.put("biz_type", "*");
        MAPPING.put("fee_rate", "*");
        MAPPING.put("fee", "*");
        MAPPING.put("cms_rate", "*");
        MAPPING.put("cms", "*");
        MAPPING.put("source", "*");
        MAPPING.put("project_name", "*");
        MAPPING.put("happen", "*");
        MAPPING.put("claim", "*");
        MAPPING.put("amount", "*");
        MAPPING.put("register_time", "*");
        MAPPING.put("bonus", "*");
        MAPPING.put("agent_name", "*");

        ENDORSE.put("company_name", "company_id");
        ENDORSE.put("operate", "*");
        ENDORSE.put("ins_type", "type");
        ENDORSE.put("biz_type", "*");
        ENDORSE.put("fee_rate", "*");
        ENDORSE.put("fee", "*");
        ENDORSE.put("cms_rate", "*");
        ENDORSE.put("cms", "*");
        ENDORSE.put("source", "*");
        ENDORSE.put("project_name", "*");
        ENDORSE.put("happen", "*");
        ENDORSE.put("claim", "*");
        ENDORSE.put("amount", "*");
        ENDORSE.put("register_time", "*");
        ENDORSE.put("bonus", "*");
        ENDORSE.put("agent_name", "*");

//        ENDORSE.put("company_name", "*");
//        ENDORSE.put("operate", "*");
//        ENDORSE.put("bonus", "*");
//        ENDORSE.put("source", "*");
//        ENDORSE.put("project_name", "*");
//        ENDORSE.put("apply_time", "*");
//        ENDORSE.put("vehicle_plate_no", "*");
//        ENDORSE.put("policy_no", "*");
//        ENDORSE.put("ins_type", "*");
//        ENDORSE.put("biz_type", "*");

        DICT.put("biz_type", JSON.parseObject("{'车险':'1'}"));
        DICT.put("ins_type", JSON.parseObject("{'交强险':'1001', '商业险':'1002'}"));
        DICT.put("operate", JSON.parseObject("{'新单':'1', '批改':'2', '退保':'3'}"));
        DICT.put("company_name", JSON.parseObject("{'人保财险':'18', '平安财险':'9', '大地财险':'28', '国寿财险':'29', '太平洋财险':'30', '信达财险':'31', '长安财险':'32', '中华联合':'33'}"));
    }

    public List<Object[]> parse(String str)
    {
        List<Object[]> tab = new ArrayList<>();

        try (InputStream is = new ByteArrayInputStream(Common.decodeBase64ToByte(str)))
        {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);

            int len = sheet.getLastRowNum();
            for (int i = 0; i < len; i++)
            {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                int cols = row.getLastCellNum();

                Object[] line = new Object[cols];
                for (int j = 0; j < cols; j++)
                {
                    Cell cell = row.getCell(j);
                    if (cell != null)
                    {
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        line[j] = cell.getStringCellValue();
                    }
                }

                boolean pass = false;
                for (int j = 0; j < line.length; j++)
                {
                    if (line[j] != null && !"".equals(Common.trimStringOf(line[j])))
                    {
                        pass = true;
                        break;
                    }
                }

                if (pass)
                    tab.add(line);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return tab;
    }
}
