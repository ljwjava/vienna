package lerrain.service.shop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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

    public List<Shop> commoditys(Long userId, String name, String type, int from, int num)
    {
        List<Shop> shops = productDao.commoditys(userId, name, type, from, num);
        for (int i=0;i<shops.size();i++){
            Shop shop = shops.get(i);
            JSONObject detailJson = new JSONObject();
            shop.setDetailJson(detailJson);
            detailJson.put("imgUrl",shop.getHomeImage());
            detailJson.put("prdCode",shop.getCommodityCode());
            detailJson.put("prdName",shop.getCommodityName());
            detailJson.put("price",shop.getPriceDesc());
            detailJson.put("promotion",shop.getPriceDescText());
            String ext = shop.getExtraInfo();
            JSONObject extraInfo = JSON.parseObject(ext);
            detailJson.put("factors",extraInfo.getJSONArray("factors"));
            detailJson.put("rules",extraInfo.getJSONArray("rules"));

            shop.setCommissionRate(this.getCommodityRate(shop.getWareId()));
        }
        return shops;
    }

    public Long getCommodityRate(Long wareId){
	    Long maxRate = 0L;
        List<JSONObject> products = productDao.queryProductsByWareId(wareId);
        for(int i=0;i<products.size();i++){
            JSONObject jsonObj = products.get(i);
            JSONArray array = JSONObject.parseArray(jsonObj.getString("content"));
            Long rate = 0L;
            for(int j=0;j<array.size();j++){
                JSONObject obj = array.getJSONObject(j);
                JSONObject rt = obj.getJSONObject("rate");
                rate = rt.getLong("primary");
                if(maxRate > rate){
                    continue;
                }else{
                    maxRate = rate;
                }
            }
        }

        return maxRate;
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
            Long tempId = json.getLong("tempId");
            json.put("usedCount",productDao.countTemplateUsed(tempId));
        }
        return rates;
    }

    public RateTemplate saveOrUpdateRateTemplate(RateTemplate rt){
	    // 修改模板
	    if(null != rt.getTempId()){
            Long tempId = productDao.saveOrUpdateRateTemplate(rt);
        }else {// 新增模板
            Long tempId = productDao.saveOrUpdateRateTemplate(rt);
            rt.setTempId(tempId);
            Long relId = productDao.saveOrUpdateRateTemplateRelation(rt);
            rt.setRelId(relId);
        }
        return rt;
    }

    public RateTemplate handleUsedRateTemp(RateTemplate rt){
	    // 已经在使用的模板设置无效
        RateTemplate contion = new RateTemplate();
        contion.setUserId(rt.getUserId());
        contion.setSubUserId(rt.getSubUserId());
        contion.setUsed("Y");
        List<JSONObject> rates = productDao.rateTemplates(contion, 0, 10);
        if(null != rates && rates.size()>0){
            for(int i=0;i<rates.size();i++) {
                RateTemplate temp = JSONObject.parseObject(rates.get(i).toJSONString(), RateTemplate.class);
                temp.setUsed("N");
                temp.setModifier(rt.getModifier());
                temp.setGmtModified(new Date());
                productDao.saveOrUpdateRateTemplateRelation(temp);
            }
        }
        return rt;
    }

    public RateTemplate deleteRateTemplate(RateTemplate rt){
        Long tempId = productDao.deleteRateTemplate(rt);
        return rt;
    }

    public List<RateTemplate> batchOperateRateTemplate(List<RateTemplate> contions){
        List<RateTemplate> rts = Lists.newArrayList();
	    for(int i=0;i<contions.size();i++) {
            RateTemplate rt = contions.get(i);
            // 已经在使用的模板设置无效
            RateTemplate res = this.handleUsedRateTemp(rt);
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
