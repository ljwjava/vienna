package lerrain.service.policy.upload;

import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class PolicyUploadDao
{
    @Autowired
    JdbcTemplate jdbc;

    public List<PolicyReady> loadBatch(String batchUUID)
    {
        List<PolicyReady> r = new ArrayList<>();

        List<Map<String, Object>> list = jdbc.queryForList("select * from t_policy_upload where batch_uuid = ? and result is null", batchUUID);
        for (Map<String, Object> m : list)
            r.add(new PolicyReady(m));

        return r;
    }

    public String upload(List<PolicyReady> list)
    {
        String batchUUID = UUID.randomUUID().toString();

        for (int j = 0; j < list.size(); j += 1000)
        {
            StringBuffer sb = new StringBuffer();
            sb.append("insert into t_policy_upload (batch_uuid, create_time, creator");
            for (String[] t : Excel.TITLE)
                sb.append(", " + t[1]);
            sb.append(") values");

            Iterator<PolicyReady> iter = list.subList(j, Math.min(list.size(), j + 1000)).iterator();
            while (iter.hasNext())
            {
                PolicyReady pr = iter.next();

                sb.append("(" + strOf(batchUUID) + ", now(), 'system'");
                for (int i = 0; i < Excel.TITLE.length; i++)
                    sb.append("," + strOf(pr.val.get(Excel.TITLE[i][1])));
                sb.append(")");

                if (iter.hasNext())
                    sb.append(",");
            }

            jdbc.update(sb.toString());
        }

        return batchUUID;
    }

    private String strOf(Object val)
    {
        if (val == null)
            return "null";

        if (val instanceof Number)
            return val.toString();

        if (val instanceof Date)
            val = Common.getString((Date)val, null);

        String str = val.toString();
        str = str.replaceAll("[\\\\]", "\\\\\\\\");
        str = str.replaceAll("[\r]", "\\\\r");
        str = str.replaceAll("[\n]", "\\\\n");
        str = str.replaceAll("[\']", "\\\\\'");
        str = str.replaceAll("[\"]", "\\\\\"");

        return "'" + str + "'";
    }

    public PolicyBase find(String policyNo, Long companyId)
    {
        if (Common.isEmpty(policyNo) || companyId == null)
            throw new RuntimeException("policyNo或companyId无法找到");

        try
        {
            Map m = jdbc.queryForMap("select * from t_policy where policy_no = ? and company_id = ? and valid is null", policyNo, companyId);
            PolicyBase pb = new PolicyBase(m);

            if (pb != null)
            {
                List<Map<String, Object>> l = jdbc.queryForList("select * from t_policy_endorse where policy_no = ? and company_id = ? and valid is null", policyNo, companyId);
                pb.setEndorse(l);
            }

            return pb;
        }
        catch (IncorrectResultSizeDataAccessException e1)
        {
            if (e1.getActualSize() > 1)
                throw new RuntimeException("有重复的保单");

            return null;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public void setResult(PolicyReady pr)
    {
        if (pr.memo != null && pr.memo.length() > 256)
            pr.memo = pr.memo.substring(0, 250) + "...";

        jdbc.update("update t_policy_upload set result = ?, memo = ? where id = ?", pr.result, pr.memo, pr.getId());
    }

    public boolean savePolicy(PolicyReady pr)
    {
        if (pr.endorse <= 0) //新单或退保
        {
            if (pr.policy == null)
            {
                List<Object> list = new ArrayList<>();
                list.add("system");
                list.add("system");

                String s1 = "platform_id, creator, create_time, updater, update_time", s2 = "6, ?, now(), ?, now()";
                for (String[] t : Excel.TITLE)
                {
                    String col = Excel.MAPPING.get(t[1]);
                    if ("*".equals(col))
                        continue;
                    if (col == null)
                        col = t[1];

                    s1 += ", " + col;
                    s2 += ", ?";

                    list.add(pr.get(col));
                }

                jdbc.update(String.format("insert into t_policy(%s) values(%s)", s1, s2), list.toArray());
            }
            else
            {
                List<Object> list = new ArrayList<>();
                list.add("system");

                String s1 = "platform_id = 6, updater = ?, update_time = now()";
                for (String[] t : Excel.TITLE)
                {
                    String col = Excel.MAPPING.get(t[1]);
                    if ("*".equals(col))
                        continue;
                    if (col == null)
                        col = t[1];
                    Object v = pr.get(col);
                    if (v == null)
                        continue;

                    s1 += ", " + col + " = ?";

                    list.add(v);
                }

                list.add(pr.policy.getId());
                jdbc.update(String.format("update t_policy set %s where id = ?", s1), list.toArray());
            }
        }
        else //批改
        {
            if (pr.policy == null)
            {
                List<Object> list = new ArrayList<>();
                list.add("system");
                list.add("system");

                String s1 = "platform_id, creator, create_time, updater, update_time", s2 = "6, ?, now(), ?, now()";
                for (String[] t : Excel.TITLE)
                {
                    String col = Excel.MAPPING.get(t[1]);
                    if ("*".equals(col))
                        continue;
                    if (col == null)
                        col = t[1];

                    s1 += ", " + col;
                    s2 += ", ?";

                    list.add(pr.get(col));
                }

                jdbc.update(String.format("insert into t_policy(%s) values(%s)", s1, s2), list.toArray());

                pr.policy = find(pr.getPolicyNo(), pr.getCompanyId());
            }

            if (pr.policy == null)
                throw new RuntimeException("该批改单找不到可以挂靠的保单");

            PolicyEndorse endorse = null;
            for (PolicyEndorse pe : pr.policy.getEndorse())
            {
                if (pe.getEndorseNo().equals(pr.getEndorseNo()))
                {
                    endorse = pe;
                    break;
                }
            }

            if (endorse == null)
            {
                List<Object> list = new ArrayList<>();
                list.add(pr.policy.getId());
                list.add("system");
                list.add("system");

                String s1 = "platform_id, policy_id, creator, create_time, updater, update_time", s2 = "6, ?, ?, now(), ?, now()";
                for (String[] t : Excel.TITLE)
                {
                    String col = Excel.ENDORSE.get(t[1]);
                    if ("*".equals(col))
                        continue;
                    if (col == null)
                        col = t[1];

                    s1 += ", " + col;
                    s2 += ", ?";

                    list.add(pr.get(col));
                }

                jdbc.update(String.format("insert into t_policy_endorse(%s) values(%s)", s1, s2), list.toArray());
            }
            else
            {
                List<Object> list = new ArrayList<>();
                list.add("system");

                String s1 = "platform_id = 6, updater = ?, update_time = now()";
                for (String[] t : Excel.TITLE)
                {
                    String col = Excel.ENDORSE.get(t[1]);
                    if ("*".equals(col))
                        continue;
                    if (col == null)
                        col = t[1];
                    Object v = pr.get(col);
                    if (v == null)
                        continue;

                    s1 += ", " + col + " = ?";

                    list.add(v);
                }

                list.add(endorse.getId());
                jdbc.update(String.format("update t_policy_endorse set %s where id = ?", s1), list.toArray());
            }
        }

        return true;
    }
}
