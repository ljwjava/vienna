package lerrain.service.policy.upload;

import com.alibaba.fastjson.JSON;
import lerrain.tool.Common;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PolicyUploadService
{
    @Autowired
    PolicyDao policyDao;

    public String upload(Long userId, List<Object[]> tab)
    {
        Object[] title = tab.get(0);

        Map<Integer, Integer> map = new HashMap<>();

        for (int j = 0; j < title.length; j++)
        {
            for (int i = 0; i < Excel.TITLE.length; i++)
            {
                String key = Excel.TITLE[i][0];
                if (key.startsWith("*"))
                    key = key.substring(1);

                if (key.equals(Common.trimStringOf(title[j])))
                {
                    map.put(j, i);
                    break;
                }
            }
        }

        for (int i = 0; i < Excel.TITLE.length; i++)
        {
            String key = Excel.TITLE[i][0];
            if (key.startsWith("*"))
            {
                boolean pass = false;
                for (int v : map.values())
                {
                    if (v == i)
                    {
                        pass = true;
                        break;
                    }
                }

                if (!pass)
                    throw new RuntimeException("必须栏目缺失" + key);
            }
        }

        List<String> err = new ArrayList();

        List<PolicyReady> list = new ArrayList<>();
        for (int i = 1; i < tab.size(); i++)
        {
            Object[] row = tab.get(i);

            try
            {
                Map m = new HashMap<>();

                for (int k = 0; k < row.length; k++)
                {
                    if (!map.containsKey(k))
                        continue;

                    int j = map.get(k);

                    if (Excel.TITLE[j].length < 3)
                    {
                        m.put(Excel.TITLE[j][1], t(row[k]));
                    }
                    else
                    {
                        String type = Excel.TITLE[j][2];
                        if ("integer".equals(type))
                            m.put(Excel.TITLE[j][1], Common.toInteger(row[k]));
                        else if ("number".equals(type))
                            m.put(Excel.TITLE[j][1], Common.toDouble(row[k]));
                        else if ("date".equals(type) || "time".equals(type))
                            m.put(Excel.TITLE[j][1], Common.dateOf(row[k]));
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

    private String t(Object v)
    {
        String r = Common.trimStringOf(v);
        if ("".equals(r))
            return null;

        return r;
    }

    public int save(String batchUUID)
    {
        int err = 0;
        int idx = 0;

        List<PolicyReady> list = policyDao.loadBatch(batchUUID);
        for (PolicyReady pr : list)
        {
            idx++;

            try
            {
                pr.prepare();

                String policyNo = pr.getString("policy_no");
                Long companyId = pr.getLong("company_id");

                PolicyBase policy = policyDao.find(policyNo, companyId);
                if (pr.compare(policy))
                {
                    if (policyDao.savePolicy(pr))
                        pr.result = 1;
                    else
                        throw new RuntimeException("保存失败");
                }
                else
                {
                    pr.result = 2;
                }

                System.out.println(idx + " " + pr.result);
            }
            catch (Exception e)
            {
                System.out.println(idx + " " + e.toString());

                err++;

                pr.result = 9;
                pr.memo = e.getMessage();
            }

            policyDao.setResult(pr);
        }

        return err;
    }

    public Runnable newTask(final Long userId, final Object[] coll)
    {
        return new Runnable()
        {
            @Override
            public void run()
            {
                Excel excel = new Excel();

                for (Object str : coll)
                {
                    List<Object[]> tab = excel.parse((String)str);

                    if (!tab.isEmpty())
                    {
                        String batchUUID = upload(userId, tab);
                        save(batchUUID);
                    }
                }
            }
        };
    }
}
