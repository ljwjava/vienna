package lerrain.service.org;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lerrain.service.common.Log;
import lerrain.tool.Common;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
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
    	Log.info("saveEnterpriseInfo request:"+JSON.toJSONString(json));
        JSONObject result = new JSONObject();
        Long id = -1l;
        int type = json.getIntValue("type");
        String email = json.getString("email");
        String loginName =json.getString("loginName");
        String mobile = (!loginName.equals(email) ? loginName:null);
        String name = json.getString("name");
        // 邀请码
        Long inviteCode = json.getLong("inviteCode");
        if(inviteCode != null && inviteCode > 0) {
            inviteCode = enterpriseSrv.getCompanyId(inviteCode);
        }
        if (1 == type) {
            // 保存渠道/子账户信息
            Member member = new Member();
            member.setName(name);
            member.setMobile(mobile);
            Long orgId = json.getLong("orgId");
            Org org = orgSrv.getOrg(orgId);
            member.setOrgId(orgId);
            member.setCompanyId(org.getCompanyId());
            member.setStatus(1);
            id = memberSrv.save(member);
        } else if (2 == type || 4 == type) {
            // 保存渠道/部门信息
            Org org = new Org();
            Long userId = json.getLong("userId");
            if(userId != null && userId > 0) {
            	org.setId(userId);
            }
            org.setName(name);
            org.setMobile(mobile);
            org.setType(4);
            Enterprise enterprise = new Enterprise();
            enterprise.setCompanyName(name);
            enterprise.setTelephone(mobile);
            enterprise.setParentId(inviteCode);
            enterprise.setEmail(email);
            Long companyId = enterpriseSrv.save(enterprise);
            org.setCompanyId(companyId);
            org.setType(4);
            org.setEmail(email);
            id = orgSrv.save(org);
        } else if (3 == type) { // 保存机构信息
            // 保存渠道/部门信息
            Org org = new Org();
            org.setName(name);
            org.setMobile(mobile);
            org.setType(4);
            org.setEmail(email);
            Long parentOrgId = json.getLong("parentOrgId");
            Org parentOrg = orgSrv.getOrg(parentOrgId);
//            // 校验层级
//            if(parentCode != null && parentCode.split("|").length > 6) {
//                result.put("result", "false");
//                result.put("content", "机构层级不能超过7层");
//                return result;
//            }
            org.setParentCode(parentOrg.getCode());
            org.setParentId(parentOrgId);
            org.setCompanyId(parentOrg.getCompanyId());
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
        int currentPage = Common.intOf(json.get("currentPage"), 1);
        int num = Common.intOf(json.get("pageSize"), 20);
        int from =  num*(currentPage - 1);
        Long count = memberSrv.countByOrgId(json.getLongValue("orgId"));
        JSONObject page = new JSONObject();
        page.put("total",count);
        page.put("pageSize", num);
        page.put("current", currentPage);
        List<Map<String, Object>> list = memberSrv.listByOrgId(json.getLongValue("orgId"), from,
                num, json.getString("searchCondition"));
        result.put("result", "success");
        JSONObject r = new JSONObject();
        r.put("pagination", page);
        r.put("list", list);
        result.put("content", r);
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
        Long companyId = json.getLongValue("companyId");
        Long userType = json.getLongValue("userType");
        if(companyId == null || companyId < 1) {
        	Long userId = json.getLongValue("userId");
        	// 获取公司ID
        	companyId = enterpriseSrv.getCompanyId(userId);
        }
        List<Enterprise> list = null;
        if(userType == 4) {
        	list = enterpriseSrv.querySubordinate(companyId);
        } else {
        	list = enterpriseSrv.querySubInfo(companyId);
        }
        result.put("result", "success");
        JSONObject rJson = new JSONObject();
        rJson.put("list", list);
        result.put("content", rJson);
        return result;
    }
    
    /**
     * 获取上传信息
     * @param json
     * @return
     */
    @RequestMapping("/upload/excel.json")
    @ResponseBody
    public JSONObject upload(@RequestBody JSONObject json) {
        JSONObject result = new JSONObject();
        JSONArray array = null;
        try {
        	Object[] coll = json.getJSONObject("files").values().toArray();
            for (Object str : coll)
            {
                array = parse((String)str);

                if (!array.isEmpty())
                {
                	Log.info("excel info:"+JSON.toJSONString(array));
                }
            }
        } catch (Exception e) {
        	Log.error(e);
        }
        result.put("result", "success");
        result.put("content", array);
        return result;
    }
    
    @RequestMapping("/batchSaveEnterpriseInfo.json")
    @ResponseBody
    public JSONObject batchSaveEnterpriseInfo(@RequestBody JSONObject json) {
        JSONObject result = new JSONObject();
        String orgId = json.getString("URI").substring(json.getString("URI").lastIndexOf("_")+1, json.getString("URI").lastIndexOf("."));
        int type = json.getIntValue("type");
        // 邀请码
        Long inviteCode = json.getLong("inviteCode");
        if(inviteCode != null && inviteCode > 0) {
            inviteCode = enterpriseSrv.getCompanyId(inviteCode);
        }
        JSONArray array = json.getJSONArray("memberInfo");
        for(int i =0;i<array.size();i++) {
            Long id = -1l;
        	JSONObject j = array.getJSONObject(i);
            String mobile = j.getString("mobile");
            String name = j.getString("name");
            if (1 == type || 0 == type) {
                // 保存渠道/子账户信息
                Member member = new Member();
                member.setName(name);
                member.setMobile(mobile);
                Org org = orgSrv.getOrg(Long.valueOf(orgId));
                member.setOrgId(Long.valueOf(orgId));
                member.setCompanyId(org.getCompanyId());
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
            j.put("id", id);
        }
        result.put("result", "success");
        result.put("content", array);
        return result;
    }    
    
    private JSONArray parse(String str)
    {
    	JSONArray array = new JSONArray();

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

                if (pass && i > 0) {
                	JSONObject json = new JSONObject();
                	json.put("name", line[1]);
                	json.put("mobile", line[2]);
                	array.add(json);
                }

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return array;
    }
    
    @RequestMapping("/companyParent.json")
    @ResponseBody
    public JSONObject companyParent(@RequestBody JSONObject json) {
        JSONObject result = new JSONObject();
        List<Enterprise> list = enterpriseSrv.querySuperior(json.getLongValue("companyId"));
        result.put("result", "success");
        result.put("content", list);
        return result;
    }
    
    @RequestMapping("/getChannel.json")
    @ResponseBody
    public JSONObject getChannel(@RequestBody JSONObject json)
    {
        Long userId = json.getLong("userId");
        Long companyId = enterpriseSrv.getCompanyId(userId);
        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", companyId != null ? enterpriseSrv.queryById(companyId) : null);
        return res;
    }
    
    @RequestMapping("/updateChannel.json")
    @ResponseBody
    public JSONObject updateChannel(@RequestBody JSONObject json)
    {
    	Enterprise enterprise = JSON.parseObject(json.toJSONString(), Enterprise.class);
        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", enterpriseSrv.updateById(enterprise));
        return res;
    }
    
    @RequestMapping("/queryChannelLink.json")
    @ResponseBody
    public JSONObject queryChannelLink(@RequestBody JSONObject json) {
        JSONObject result = new JSONObject();
        result.put("result", "success");
        Long userId = json.getLong("userId");
        result.put("content", userId);
        return result;
    }
}
