package lerrain.service.template;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lerrain.service.common.Log;
import lerrain.tool.Common;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 商城模板服务
 */
@Controller
public class TemplateController {

    @Autowired
    private TemplateService templateSrv;

    @RequestMapping("/templates.json")
    @ResponseBody
    public JSONObject templates(@RequestBody JSONObject p) {
        int currentPage = Common.intOf(p.get("currentPage"), 1);
        int num = Common.intOf(p.get("pageSize"), 10);
        int from = num * (currentPage - 1);

        JSONArray list = new JSONArray();
        for (Template template : templateSrv.list(null, from, num)) {
            JSONObject obj = new JSONObject();
            obj.put("id", template.getId());
            obj.put("templateName", template.getTemplateName());
            obj.put("gmtModified", template.getGmtModified());
            list.add(obj);
        }
        JSONObject r = new JSONObject();
        r.put("list", list);

        JSONObject page = new JSONObject();
        page.put("total", templateSrv.count(null));
        page.put("pageSize", num);
        page.put("current", currentPage);
        r.put("pagination", page);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", r);
        return res;
    }

    @RequestMapping("/findById.json")
    @ResponseBody
    public JSONObject findById(@RequestBody JSONObject p) {
        Long templateId = p.getLong("templateId");

        Template template = templateSrv.findByTemplateId(templateId);
        JSONObject result = (JSONObject) JSON.toJSON(template);
        if (result == null) {
            result = new JSONObject();
            return result;
        }
        String banner = template.getBanner();
        String message = template.getMessage();
        result.put("banner", StringUtils.isNotBlank(banner) ? JSON.parseArray(banner) : null);
        result.put("message", StringUtils.isNotBlank(message) ? JSON.parseArray(message) : null);
        List<TemplateProductRelation> ids = templateSrv.queryProductIdByTemplateId(templateId);
        List<Long> idList = Lists.newArrayList();
        for (int i = 0; i < ids.size(); i++) {
            idList.add(ids.get(i).getProductId());
        }
        List<TemplateProduct> tps = templateSrv.queryTps(idList);

        List<TemplateProductType> tpts = templateSrv.queryByProductId(idList);

        result.put("proType", tpts);
        result.put("product", tps);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", result);
        return res;
    }

    @RequestMapping("/delete.json")
    @ResponseBody
    public JSONObject delete(@RequestBody JSONObject p) {
        Long templateId = p.getLong("templateId");
        templateSrv.delete(templateId);

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }

    @RequestMapping("/savePro.json")
    @ResponseBody
    public JSONObject saveProduct(@RequestBody JSONObject p) {
        Log.info(p);
        TemplateProduct tp = buildTemplatePro(p);
        Long temProductId = templateSrv.saveOrUpdateTemplateProduct(tp);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", temProductId);
        return res;
    }


    @RequestMapping("/proTypes.json")
    @ResponseBody
    public JSONObject productTypes(@RequestBody JSONObject p) {
        int currentPage = Common.intOf(p.get("currentPage"), 1);
        int num = Common.intOf(p.get("pageSize"), 10);
        int from = num * (currentPage - 1);

        JSONArray list = new JSONArray();
        for (TemplateProductType productType : templateSrv.listProType(null, from, num)) {
            JSONObject obj = new JSONObject();
            obj.put("id", productType.getId());
            obj.put("productTypeName", productType.getProductTypeName());
            obj.put("gmtModified", productType.getGmtModified());
            list.add(obj);
        }
        JSONObject r = new JSONObject();
        r.put("list", list);

        JSONObject page = new JSONObject();
        page.put("total", templateSrv.countProType(null));
        page.put("pageSize", num);
        page.put("current", currentPage);
        r.put("pagination", page);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", r);
        return res;
    }

    @RequestMapping("/saveProType.json")
    @ResponseBody
    public JSONObject saveProType(@RequestBody JSONObject p) {
        Log.info(p);
        Long typeId = p.getLong("id");
        String productTypeName = p.getString("productTypeName");

        TemplateProductType tpt = new TemplateProductType();
        tpt.setId(typeId);
        tpt.setProductTypeName(productTypeName);
        Long id = templateSrv.saveOpUpdateProType(tpt);
        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", id);

        return res;
    }


    @RequestMapping("/save.json")
    @ResponseBody
    public JSONObject save(@RequestBody JSONObject p) {
        Log.info(p);
        Long userId = p.getLong("userId");
        Long templateId = p.getLong("id");
        String templateName = p.getString("templateName");
        String title = p.getString("title");
        JSONArray message = p.getJSONArray("msg");
        JSONArray banner = p.getJSONArray("banner");
        JSONObject proType = p.getJSONObject("proType");
        JSONArray products = p.getJSONArray("product");

        Long proTypeId = proType != null ? proType.getLong("id") : null;
        if (banner == null) {
            banner = new JSONArray();
            JSONObject json = new JSONObject();
            json.put("picUrl", "111");
            json.put("launchUrl", "222");
            banner.add(json);
        }

        //创建模板主表
        Template t = new Template();
        t.setId(templateId);
        t.setTemplateName(StringUtils.isNotBlank(templateName) ? templateName : "商城模板");
        t.setTitle(StringUtils.isNotBlank(title) ? title : "商城");
        t.setMessage(message != null ? message.toJSONString() : null);
        t.setBanner(banner.toJSONString());
        Long id = templateSrv.saveOpUpdate(t);

        //创建产品
        List<Long> proIds = Lists.newArrayList();
        if (products != null && products.size() > 0) {
            for (int i = 0; i < products.size(); i++) {
                TemplateProduct tp = buildTemplatePro(products.getJSONObject(i));
                Long productId = templateSrv.saveOrUpdateTemplateProduct(tp);
                proIds.add(productId);
            }
        } else {
            //设置默认产品
        }

        //创建模板产品关系表  创建产品类型与产品关系表
        if (proIds.size() > 0) {
            templateSrv.removeTpRelation(id);
            List<TemplateProductRelation> tpist = Lists.newArrayList();
            List<TemplateProductTypeRelation> tptrList = Lists.newArrayList();
            for (Long productId : proIds) {
                TemplateProductRelation tpr = new TemplateProductRelation();
                tpr.setProductId(productId);
                tpr.setTemplateId(id);
                tpist.add(tpr);
                if (proTypeId != null) {
                    TemplateProductTypeRelation tptr = new TemplateProductTypeRelation();
                    tptr.setProductTypeId(proTypeId);
                    tptr.setProductId(productId);
                    tptrList.add(tptr);
                }
            }
            templateSrv.batchSaveTpRelation(tpist);
            if (tptrList.size() > 0) {
                templateSrv.removeProTypeRelation(proTypeId);
                templateSrv.batchSaveProTypeRelation(tptrList);
            }
        }

        //创建商城模板与用户关系表
        if (templateSrv.queryByTUserId(id, userId) == null) {
            TemplateUserRelation tur = new TemplateUserRelation();
            tur.setUserId(userId);
            tur.setTemplateId(id);
            templateSrv.saveTUserRelation(tur);
        }
        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", id);

        return res;
    }


    private TemplateProduct buildTemplatePro(JSONObject p) {
        Long id = p.getLong("id");
        String packageName = p.getString("packageName");
        String proDesc = p.getString("proDesc");
        String premium = p.getString("premium");
        String link = p.getString("link");
        String pic = p.getString("pic");
        String labelName = p.getString("labelName");
        String buttonName = p.getString("buttonName");
        String buttonLink = p.getString("buttonLink");
        String isIndex = p.getString("isIndex");
        String indexPic = p.getString("indexPic");

        TemplateProduct tp = new TemplateProduct();
        tp.setId(id);
        tp.setPackageName(packageName);
        tp.setProDesc(proDesc);
        tp.setPremium(premium);
        tp.setLink(link);
        tp.setPic(pic);
        tp.setLabel(labelName);
        tp.setButtonName(buttonName);
        tp.setButtonLink(buttonLink);
        tp.setIsIndex(isIndex);
        tp.setIndexPic(indexPic);
        return tp;
    }

}
