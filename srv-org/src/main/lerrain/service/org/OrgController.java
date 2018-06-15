package lerrain.service.org;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Controller
public class OrgController
{
    @Autowired
    OrgService orgSrv;
    @Autowired
    MemberService memberSrv;
    @Autowired
    EnterpriseService enterpriseSrv;

    @RequestMapping("/member.json")
    @ResponseBody
    public JSONObject member(@RequestBody JSONObject json)
    {
        Long memberId = json.getLong("memberId");
        Long userId = json.getLong("userId");
        int userType = json.getIntValue("userType");

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", memberId != null ? orgSrv.getMember(memberId) : orgSrv.findMember(userId, userType));

        return res;
    }

    @RequestMapping("/find.json")
    @ResponseBody
    public JSONObject find(@RequestBody JSONObject json)
    {
        JSONObject r = new JSONObject();

        JSONArray m1 = json.getJSONArray("member");
        if (m1 != null)
        {
            JSONArray m2 = new JSONArray();
            for (int i = 0; i < m1.size(); i++)
            {
                Member member = orgSrv.getMember(m1.getLong(i));
                m2.add(member);
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
                o2.add(org);
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
                c2.add(company);
            }

            r.put("company", c2);
        }

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", r);

        return res;
    }

    @RequestMapping("/saveEnterpriseInfo.json")
    @ResponseBody
    public JSONObject saveEnterpriseInfo(@RequestBody JSONObject json) {
        JSONObject result = new JSONObject();
        Long id = -1l;
        int type = json.getIntValue("type");
        String mobile = json.getString("mobile");
        String name = json.getString("name");
        // 邀请码
        Long inviteCode = json.getLong("inviteCode");
        if (1 == type) {
            // 保存渠道/子账户信息
            Member member = new Member();
            member.setName(name);
            member.setMobile(mobile);
            Long orgId = json.getLong("orgId");
            Long companyId = json.getLong("companyId");
            member.setOrgId(orgId);
            member.setCompanyId(companyId);
            member.setStatus(1);
            id = memberSrv.save(member);
        } else if (2 == type) {
            // 保存渠道/部门信息
            Org org = new Org();
            org.setName(name);
            org.setMobile(mobile);
            org.setType(4);
            Enterprise enterprise = new Enterprise();
            enterprise.setCompanyName(name);
            enterprise.setTelephone(mobile);
            enterprise.setParentId(inviteCode);
            Long companyId = enterpriseSrv.save(enterprise);
            org.setCompanyId(companyId);
            org.setType(4);
            id = orgSrv.save(org);
        } else if (3 == type) { // 保存机构信息
            // 保存渠道/部门信息
            Org org = new Org();
            org.setName(name);
            org.setMobile(mobile);
            org.setType(4);
            Long companyId = json.getLong("companyId");
            Long parentOrgId = json.getLong("parentOrgId");
            org.setParentId(parentOrgId);
            org.setCompanyId(companyId);
            org.setType(4);
            id = orgSrv.save(org);
        }
        result.put("result", "success");
        result.put("content", id);
        return result;
    }

    @RequestMapping("/updMember.json")
    @ResponseBody
    public JSONObject updMember(@RequestBody JSONObject json) {
        JSONObject result = new JSONObject();
        Member member = JSON.parseObject(json.toJSONString(), Member.class);
        boolean flag = memberSrv.update(member);
        result.put("result", "success");
        result.put("content", flag);

        return result;
    }

    @RequestMapping("/updOrg.json")
    @ResponseBody
    public JSONObject updOrg(@RequestBody JSONObject json) {
        JSONObject result = new JSONObject();
        Org org = JSON.parseObject(json.toJSONString(), Org.class);
        boolean flag = orgSrv.update(org);
        result.put("result", "success");
        result.put("content", flag);

        return result;
    }

    @RequestMapping("/removeOrg.json")
    @ResponseBody
    public JSONObject removeOrg(@RequestBody JSONObject json) {
        JSONObject result = new JSONObject();
        Long id = json.getLong("id");
        boolean flag = orgSrv.remove(id);
        result.put("result", "success");
        result.put("content", flag);

        return result;
    }

    @RequestMapping("/childOrg.json")
    @ResponseBody
    public JSONObject childOrg(@RequestBody JSONObject json) {
        JSONObject result = new JSONObject();
        List<Org> org = orgSrv.childOrg(json.getLongValue("orgId"));
        result.put("result", "success");
        result.put("content", org);

        return result;
    }

    @RequestMapping("/memberCountByOrgId.json")
    @ResponseBody
    public JSONObject countByOrgId(@RequestBody JSONObject json) {
        JSONObject result = new JSONObject();
        Long count = memberSrv.countByOrgId(json.getLongValue("orgId"));
        result.put("result", "success");
        result.put("content", count);
        return result;
    }

    @RequestMapping("/memberListByOrgId.json")
    @ResponseBody
    public JSONObject listByOrgId(@RequestBody JSONObject json) {
        JSONObject result = new JSONObject();
        List<Map<String, Object>> list = memberSrv.listByOrgId(json.getLongValue("orgId"), json.getInteger("from"),
                json.getInteger("number"), json.getString("searchCondition"));
        result.put("result", "success");
        result.put("content", list);
        return result;
    }

    @RequestMapping("/orgTree.json")
    @ResponseBody
    public JSONObject orgTree(@RequestBody JSONObject json) {
        JSONObject result = new JSONObject();
        JSONArray orgTreeJson = orgSrv.orgTree(json.getLongValue("orgId"));
        result.put("result", "success");
        result.put("content", orgTreeJson);

        return result;
    }

    @RequestMapping("/companySubordinate.json")
    @ResponseBody
    public JSONObject companySubordinate(@RequestBody JSONObject json) {
        JSONObject result = new JSONObject();
        List<Enterprise> list = enterpriseSrv.querySubordinate(json.getLongValue("companyId"));
        result.put("result", "success");
        JSONObject rJson = new JSONObject();
        rJson.put("list", list.size());
        rJson.put("list", list);
        result.put("content", rJson);
        return result;
    }
}
