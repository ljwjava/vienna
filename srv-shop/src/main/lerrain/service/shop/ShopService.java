package lerrain.service.shop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ShopService
{
	@Autowired
	ShopDao productDao;

	public void reset()
	{
	}

    public int count(String search)
    {
        return productDao.count(search);
    }

    public List<JSONObject> types(String search, int from, int num)
    {
        List<JSONObject> types = productDao.types(search, from, num);
        return types;
    }

    public List<JSONObject> commoditys(String search, int from, int num)
    {
        List<JSONObject> objs = Lists.newArrayList();
        List<Shop> shops = productDao.commoditys(search, from, num);
        for (int i=0;i<shops.size();i++){
            JSONObject shop = (JSONObject) JSON.toJSON(shops.get(i));
            //shop.setSupplierInfo(); -- to do 获取供应商信息

            JSONObject json = new JSONObject();
            json.put("id",shop.getLong("commodityId"));
            json.put("productId",shop.getLong("commodityId"));
            json.put("creator",shop.getString("creator"));
            json.put("type",shop.getString("commodityTypeCode"));
            json.put("dictKey",shop.getString("commodityCode"));
            json.put("gmtCreated",shop.getString("gmtCreated"));
            json.put("modifier",shop.getString("modifier"));
            json.put("gmtModified",shop.getString("gmtModified"));
            json.put("isDeleted",shop.getString("isDeleted"));
            json.put("sortIndex",shop.getString("sort"));
            JSONObject detailJson = new JSONObject();
            json.put("detailJson",detailJson);
            detailJson.put("imgUrl",shop.getString("homeImage"));
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
}
