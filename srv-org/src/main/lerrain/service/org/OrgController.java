package lerrain.service.org;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class OrgController
{
    @Autowired
    OrgService orgSrv;

    @RequestMapping("/member.json")
    @ResponseBody
    public JSONObject member(@RequestBody JSONObject json)
    {
        Long memberId = json.getLong("memberId");
        Long userId = json.getLong("userId");

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", memberId != null ? orgSrv.getMember(memberId) : orgSrv.findMember(userId));

        return res;
    }

    @RequestMapping("/name.json")
    @ResponseBody
    public JSONObject name(@RequestBody JSONObject json)
    {
        JSONObject r = new JSONObject();

        JSONArray m1 = json.getJSONArray("member");
        if (m1 != null)
        {
            JSONArray m2 = new JSONArray();
            for (int i = 0; i < m1.size(); i++)
            {
                Member member = orgSrv.getMember(m1.getLong(i));
                m2.add(member == null ? null : member.getName());
            }

            r.put("member", m2);
        }

        JSONArray o1 = json.getJSONArray("org");
        if (o1 != null)
        {
            JSONArray o2 = new JSONArray();
            for (int i = 0; i < o1.size(); i++)
            {
                Org org = orgSrv.getOrg(o1.getLong(i));
                o2.add(org == null ? null : org.getName());
            }

            r.put("org", o2);
        }

        JSONArray c1 = json.getJSONArray("company");
        if (c1 != null)
        {
            JSONArray c2 = new JSONArray();
            for (int i = 0; i < c1.size(); i++)
            {
                Company company = orgSrv.getCompany(c1.getLong(i));
                c2.add(company == null ? null : company.getName());
            }

            r.put("company", c2);
        }

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", r);

        return res;
    }
}
