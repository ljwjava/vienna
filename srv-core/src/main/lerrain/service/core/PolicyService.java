package lerrain.service.core;

import com.alibaba.fastjson.JSON;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PolicyService
{
    @Autowired
    PolicyDao policyDao;

    public String upload(List<Object[]> tab)
    {
        Object[] title = tab.get(0);

        for (int i = 0; i < Excel.TITLE.length; i++)
        {
            if (!Excel.TITLE[i][0].equals(Common.trimStringOf(title[i])))
                throw new RuntimeException("格式错误");
        }

        List<String> err = new ArrayList();

        List<PolicyReady> list = new ArrayList<>();
        for (int i = 1; i < tab.size(); i++)
        {
            Object[] row = tab.get(i);

            try
            {
                Map m = new HashMap<>();
                for (int j = 0; j < Excel.TITLE.length && j < row.length; j++)
                {
                    if (Excel.TITLE[j].length < 3)
                    {
                        m.put(Excel.TITLE[j][1], Common.trimStringOf(row[j]));
                    }
                    else
                    {
                        String type = Excel.TITLE[j][2];
                        if ("integer".equals(type))
                            m.put(Excel.TITLE[j][1], Common.toInteger(row[j]));
                        else if ("number".equals(type))
                            m.put(Excel.TITLE[j][1], Common.toDouble(row[j]));
                        else if ("date".equals(type) || "time".equals(type))
                            m.put(Excel.TITLE[j][1], Common.dateOf(row[j]));
                    }
                }

                PolicyReady pr = new PolicyReady(m);
                String res = pr.verify();

                if (res != null)
                    err.add("上传错误：" + res + " - " + JSON.toJSON(row));
                else
                    list.add(pr);
            }
            catch (Exception e)
            {
                err.add("上传错误：" + e.toString() + " - " + JSON.toJSON(row));
            }
        }

        if (!err.isEmpty())
            throw new RuntimeException(JSON.toJSONString(err));

        return policyDao.upload(list);
    }

    public int save(String batchUUID)
    {
        int err = 0;

        List<PolicyReady> list = policyDao.loadBatch(batchUUID);
        for (PolicyReady pr : list)
        {
            try
            {
                pr.prepare();

                String policyNo = pr.getString("policy_no");
                Long companyId = pr.getLong("company_id");

                Policy policy = policyDao.find(policyNo, companyId);
                if (pr.compare(policy))
                {
                    if (!policyDao.savePolicy(pr))
                        throw new RuntimeException("保存失败");
                }
            }
            catch (Exception e)
            {
                err++;

                pr.result = 9;
                pr.memo = e.getMessage();
            }

            policyDao.setResult(pr);
        }

        return err;
    }

    public Long getCompanyId(String name)
    {
        return null;
    }
}
