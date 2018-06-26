package lerrain.service.shop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
public class ShopService
{
	@Autowired
	ShopDao productDao;

	public void reset()
	{
	}

    public int count(Long userId, String name, String type)
    {
        return productDao.count(userId, name, type);
    }

    public int countType(String search)
    {
        return productDao.countType(search);
    }

    public List<JSONObject> types(String search, int from, int num)
    {
        List<JSONObject> types = productDao.types(search, from, num);
        return types;
    }

    public List<JSONObject> commoditys(Long userId, String name, String type, int from, int num)
    {
        List<JSONObject> objs = Lists.newArrayList();
        List<Shop> shops = productDao.commoditys(userId, name, type, from, num);
        for (int i=0;i<shops.size();i++){
            JSONObject shop = (JSONObject) JSON.toJSON(shops.get(i));
            //shop.setSupplierInfo(); -- to do 获取供应商信息

            JSONObject json = new JSONObject();
            json.put("id",shop.getLong("commodityId"));
            json.put("productId",shop.getLong("commodityId"));
            json.put("userId",shop.getLong("orgId"));
            json.put("creator",shop.getString("creator"));
            json.put("type",shop.getString("commodityTypeCode"));
            json.put("typeName",shop.getString("commodityTypeName"));
            json.put("dictKey",shop.getString("commodityCode"));
            json.put("name",shop.getString("commodityName"));
            json.put("gmtCreated",shop.getString("gmtCreated"));
            json.put("modifier",shop.getString("modifier"));
            json.put("gmtModified",shop.getString("gmtModified"));
            json.put("isDeleted",shop.getString("isDeleted"));
            json.put("sortIndex",shop.getString("sort"));
            JSONObject detailJson = new JSONObject();
            json.put("detailJson",detailJson);
            detailJson.put("imgUrl",shop.getString("homeImage"));
            detailJson.put("prdCode",shop.getString("commodityCode"));
            detailJson.put("prdName",shop.getString("commodityName"));
            detailJson.put("price",shop.getString("priceDesc"));
            detailJson.put("promotion",shop.getString("priceDescText"));
            String ext = shop.getString("extraInfo");
            JSONObject extraInfo = JSON.parseObject(ext);
            detailJson.put("factors",extraInfo.getJSONArray("factors"));
            detailJson.put("rules",extraInfo.getJSONArray("rules"));
            objs.add(json);
        }
        return objs;
    }

    public int countTemplate(RateTemplate contion)
    {
        return productDao.countTemplate(contion);
    }

    public List<JSONObject> rateTemplates(RateTemplate contion, int from, int num)
    {
        List<JSONObject> rates = productDao.rateTemplates(contion, from, num);
        for(int i=0;i<rates.size();i++){
            JSONObject json = rates.get(i);
            Long tempId = json.getLong("rel_temp_id");
            json.put("usedCount",productDao.countTemplateUsed(tempId));
        }
        return rates;
    }

    public RateTemplate saveOrUpdateRateTemplate(RateTemplate rt){
        Long tempId = productDao.saveOrUpdateRateTemplate(rt);
        rt.setTempId(tempId);
        Long relId = productDao.saveOrUpdateRateTemplateRelation(rt);
        rt.setRelId(relId);
        return rt;
    }

    public RateTemplate updateRateTemplate4User(RateTemplate rt){
	    // 已经在使用的模板设置无效
        RateTemplate contion = new RateTemplate();
        contion.setUserId(rt.getUserId());
        contion.setSubUserId(rt.getSubUserId());
        contion.setUsed("Y");
        List<JSONObject> rates = productDao.rateTemplates(contion, 0, 10);
        if(null != rates && rates.size()>0){
            RateTemplate temp = JSONObject.parseObject(rates.get(0).toJSONString(), RateTemplate.class);
            temp.setUsed("N");
            temp.setModifier(rt.getModifier());
            temp.setGmtModified(new Date());
            productDao.saveOrUpdateRateTemplateRelation(rt);
        }
        // 重新关联一个有效的模板
        rt.setRelId(null);
        Long relId = productDao.saveOrUpdateRateTemplateRelation(rt);
        rt.setRelId(relId);
        return rt;
    }

    public List<RateTemplate> batchOperateRateTemplate(List<RateTemplate> contions){
        List<RateTemplate> rts = Lists.newArrayList();
	    for(int i=0;i<contions.size();i++) {
            RateTemplate rt = contions.get(i);
            Long relId = productDao.saveOrUpdateRateTemplateRelation(rt);
            rt.setRelId(relId);
            rts.add(rt);
        }
        return rts;
    }

    public Shop queryShopById(Long wareId)
    {
        Shop shop = productDao.queryShopById(wareId);
        return shop;
    }
}
