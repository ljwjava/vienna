package lerrain.service.template;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lerrain.service.common.Log;
import lerrain.tool.Common;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        String name = p.getString("name");
        Long userId = p.getLong("userId");
        JSONArray list = new JSONArray();
        for (Template template : templateSrv.list(name, userId, from, num)) {
            JSONObject obj = new JSONObject();
            obj.put("id", template.getId());
            obj.put("templateName", template.getTemplateName());
            obj.put("gmtModified", template.getGmtModified());
            obj.put("link", template.getLink());
            list.add(obj);
        }
        JSONObject r = new JSONObject();
        r.put("list", list);

        JSONObject page = new JSONObject();
        page.put("total", templateSrv.count(name, userId));
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
        result.put("msg", StringUtils.isNotBlank(message) ? JSON.parseArray(message) : null);

        JSONObject pJson = findProducts(p);
        result.put("typeProducts", pJson != null ? pJson.getJSONArray("content") : new JSONArray());
        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", result);
        return res;
    }

    @RequestMapping("/delete.json")
    @ResponseBody
    public JSONObject delete(@RequestBody JSONObject p) {
        Long templateId = p.getLong("templateId");
        //删除模板
        templateSrv.delete(templateId);
        //删除模板产品关系表
        templateSrv.removeTpRelation(templateId);
        //删除模板用户关系表
        templateSrv.removeTurRelation(templateId);
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
        if (p == null) {
            return new JSONObject();
        }
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

    @RequestMapping("/findProducts.json")
    @ResponseBody
    public JSONObject findProducts(@RequestBody JSONObject p) {
        Long templateId = p.getLong("templateId");
        JSONObject result = new JSONObject();
        if (templateId == null) {
            return result;
        }
        List<TemplateProductRelation> ids = templateSrv.queryProductIdByTemplateId(templateId);
        List<Long> idList = Lists.newArrayList();
        for (int i = 0; i < ids.size(); i++) {
            idList.add(ids.get(i).getProductId());
        }
        if (idList.size() <= 0) {
            return result;
        }
        List<TemplateProduct> tps = templateSrv.queryTps(idList);
        List<TemplateProductType> tpts = templateSrv.queryByProductId(idList);
        List<TemplateProductTypeRelation> tptrs = templateSrv.queryTptrsByTemplateIdAndProductId(templateId, idList);
        JSONArray typeProducts = new JSONArray();
        for (TemplateProductTypeRelation tptr : tptrs) {
            JSONObject pt = new JSONObject();
            JSONArray prodsArr = new JSONArray();
            Long pTypeId = tptr.getProductTypeId();
            Long prodId = tptr.getProductId();
            for (TemplateProductType tpt : tpts) {
                if (tpt.getId().longValue() == pTypeId.longValue()) {
                    pt.put("proType", tpt);
                    break;
                }
            }
            for (TemplateProduct tp : tps) {
                if (tp.getId().longValue() == prodId.longValue()) {
                    prodsArr.add(tp);
                }
            }
            pt.put("product", prodsArr);
            typeProducts.add(pt);
        }
        result.put("content", typeProducts);
        result.put("result", "success");
        return result;
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
        JSONArray typeProducts = p.getJSONArray("typeProducts");
        String link = p.getString("link");
        if (banner == null) {
            banner = new JSONArray();
            JSONObject json = new JSONObject();
//            默认的banner图
            json.put("picUrl", "https://static.iyunbao.com/website/health/iybApp/cloud/h5/home/home_bac.png");
//            json.put("launchUrl", "");
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
        t.setId(id);
        link = link + "?accountId=" + userId + "&templateId=" + id;
        t.setLink(link);
        templateSrv.saveOpUpdate(t);

        //创建产品
        List<Long> proIds;
        if (typeProducts != null && typeProducts.size() > 0) {
            //先删除关系 在重新添加
            templateSrv.removeTpRelation(id);
            Long pTypeId = null;
            for (int i = 0; i < typeProducts.size(); i++) {
                proIds = Lists.newArrayList();
                JSONObject typeProduct = typeProducts.getJSONObject(i);
                JSONObject pType = typeProduct.getJSONObject("proType");
                JSONObject ptJson = saveProType(pType);
                pTypeId = ptJson != null ? ptJson.getLong("content") : null;

                JSONArray products = typeProduct.getJSONArray("product");
                if (products != null && products.size() > 0) {
                    for (int j = 0; j < products.size(); j++) {
                        TemplateProduct tp = buildTemplatePro(products.getJSONObject(j));
                        Long productId = templateSrv.saveOrUpdateTemplateProduct(tp);
                        proIds.add(productId);
                    }
                } else {
                    //设置默认产品
                }
                //创建模板产品关系表  创建产品类型与产品关系表
                if (proIds.size() > 0) {
                    List<TemplateProductRelation> tpList = Lists.newArrayList();
                    List<TemplateProductTypeRelation> tptrList = Lists.newArrayList();
                    for (Long productId : proIds) {
                        TemplateProductRelation tpr = new TemplateProductRelation();
                        tpr.setProductId(productId);
                        tpr.setTemplateId(id);
                        tpList.add(tpr);
                        if (pTypeId != null) {
                            TemplateProductTypeRelation tptr = new TemplateProductTypeRelation();
                            tptr.setProductTypeId(pTypeId);
                            tptr.setProductId(productId);
                            tptr.setTemplateId(id);
                            tptrList.add(tptr);
                        }
                    }
                    templateSrv.batchSaveTpRelation(tpList);
                    if (tptrList.size() > 0) {
                        templateSrv.removeProTypeRelation(pTypeId);
                        templateSrv.batchSaveProTypeRelation(tptrList);
                    }
                }
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


    @RequestMapping("/handle.json")
    @ResponseBody
    public JSONObject handleTemplate(@RequestBody JSONObject p) {
        Log.info(p);
        Long userId = p.getLong("userId");
        JSONArray banner = p.getJSONArray("banner");
        JSONArray typeProducts = p.getJSONArray("typeProducts");
        String link = p.getString("shopUrl");
        if (banner == null) {
            banner = new JSONArray();
            JSONObject json = new JSONObject();
//            默认的banner图
            json.put("picUrl", "https://static.iyunbao.com/website/health/iybApp/cloud/h5/home/home_bac.png");
//            json.put("launchUrl", "");
            banner.add(json);
        }
        JSONObject pp = new JSONObject();
        pp.put("templateName", "商城模板");
        pp.put("title", "商城");
        pp.put("banner", banner);
        pp.put("userId", userId);
        pp.put("link", link);
        JSONArray array = new JSONArray();
        Map<String, JSONArray> map = Maps.newTreeMap();
        if (typeProducts != null && typeProducts.size() > 0) {
            for (int i = 0; i < typeProducts.size(); i++) {
                JSONObject typeProduct = typeProducts.getJSONObject(i);
                String typeName = typeProduct.getString("typeName");
                JSONArray productArray = new JSONArray();
                for (int j = 0; j <= typeProducts.size() - 1; j++) {
                    JSONObject tpj = typeProducts.getJSONObject(j);
                    String tNameJ = tpj.getString("typeName");
                    JSONObject pro = new JSONObject();
                    if (Objects.equals(typeName, tNameJ)) {
                        pro.put("packageName", tpj.getString("name"));
                        pro.put("premium", tpj.getString("price"));
                        if (j <= 1) {
                            //默认前2个做首页
                            pro.put("isIndex", "Y");
                            pro.put("indexPic", "aaaaaaaa");
                        }
                        productArray.add(pro);
                    }
                }
                map.put(typeName, productArray);
            }
        }
        for (String key : map.keySet()) {
            JSONObject json = new JSONObject();
            JSONObject pType = new JSONObject();
            pType.put("productTypeName", key);
            json.put("proType", pType);
            json.put("product", map.get(key));
            array.add(json);
        }
        pp.put("typeProducts", array);
        return save(pp);
    }


    private TemplateProduct buildTemplatePro(JSONObject p) {
        Long id = p.getLong("id");
        String packageName = p.getString("packageName");
        String proDesc = p.getString("proDesc");
        String premium = p.getString("premium");
        String link = p.getString("link");
        String pic = p.getString("pic");
        String labelName = p.getString("label");
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
