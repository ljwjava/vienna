package lerrain.service.shop;

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

    public List<Shop> list(String search, int from, int num)
    {
        List<Shop> shops = productDao.list(search, from, num);
        for (int i=0;i<shops.size();i++){
            Shop shop = shops.get(i);
            //shop.setSupplierInfo(); -- to do 获取供应商信息
            String extraInfo = shop.getExtraInfo();
            JSONObject json = JSONObject.parseObject(extraInfo);
            shop.setFactors(json.getJSONArray("factors"));
            shop.setRules(json.getJSONArray("rules"));
        }
        return shops;
    }
}
