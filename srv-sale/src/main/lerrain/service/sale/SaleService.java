package lerrain.service.sale;

import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.ServiceMgr;
import lerrain.tool.Common;
import lerrain.tool.formula.Formula;
import lerrain.tool.script.Stack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SaleService
{
    @Autowired
    SaleDao saleDao;

    @Autowired
    ServiceMgr serviceMgr;

    Map<Object, PackIns> packs;

    Map<Object, Ware> map;

    Map<Long, Map> vendorMap;

//    Map<String, List<Ware>> temp;

    public void reset()
    {
        vendorMap = new HashMap<>();
        for (Map map : saleDao.loadAllVendor())
            vendorMap.put(Common.toLong(map.get("id")), map);

//        temp = new HashMap<>();
        map = new HashMap<>();

        List<Ware> list = saleDao.loadAll();

        for (Ware c : list)
        {
            map.put(c.getId(), c);
            map.put(c.getCode(), c);

            c.setDetail(new ArrayList<PackIns>());
        }

        packs = new HashMap<>();
        for (PackIns pack : saleDao.loadPacks(map, vendorMap))
        {
            if (pack.getWare() != null)
                pack.getWare().getDetail().add(pack);

            packs.put(pack.getId(), pack);
            packs.put(pack.getCode(), pack);
        }
    }

    private Ware filterPack(Ware w, List<Long> packIds){
        if(w != null){
            if(packIds != null && packIds.size() > 0){
                List<PackIns> rpis = null;
                List<PackIns> pis = w.getDetail();
                if(pis != null) {
                    for(PackIns pi : pis) {
                        if(packIds.contains(pi.getId()) && pi.canShow()){
                            if(rpis == null) rpis = new ArrayList<PackIns>();
                            rpis.add(pi);
                        }
                    }
                }
                w.setDetail(rpis);
            }
        }
        return w;
    }

    public Ware getWare(Long id)
    {
        return map.get(id);
    }
    public Ware getWare(Long id, List<Long> packIds)
    {
        Ware w = getWare(id);
        if(w != null){
            w = w.clone();
            w = filterPack(w, packIds);
        }

        return w;
    }

    public Ware getWare(String code)
    {
        return map.get(code);
    }
    public Ware getWare(String code, List<Long> packIds)
    {
        Ware w = getWare(code);
        if(w != null){
            w = w.clone();
            w = filterPack(w, packIds);
        }

        return w;
    }

    public Map getVendor(Long vendorId)
    {
        return vendorMap.get(vendorId);
    }

//    public List<Ware> find(Long platformId, String tag)
//    {
//        if (platformId == null)
//            return null;
//
//        String key = platformId + "/" + tag;
//
//        if (temp.containsKey(key))
//            return temp.get(key);
//
//        List<Ware> r = new ArrayList<>();
//
//        for (Long id : cd.find(platformId))
//        {
//            Ware c = map.get(id);
//            if (c != null && c.match(tag))
//                r.add(c);
//        }
//
//        temp.put(tag, r);
//
//        return r;
//    }

    public PackIns getPack(Long packId)
    {
        return packs.get(packId);
    }

    public PackIns getPack(String packCode)
    {
        return packs.get(packCode);
    }

    private Stack factorsOf(PackIns packIns, Map<String, Object> vals)
    {
        if (packIns.getInputForm() != null) for (InputField field : packIns.getInputForm())
        {
            String name = field.getName();
            Object value = vals.get(name);

            if (value == null)
                continue;

            vals.put(name, PackUtil.translate(field.getType(), value));
        }

        Stack stack = new Stack(packIns.getStack());
        stack.declare("self", vals);

        return stack;
    }

    public Object perform(Formula opt, PackIns packIns, Map<String, Object> vals)
    {
        Stack stack = factorsOf(packIns, vals);
        return opt.run(stack);
    }

    public Map<String, Object> getPrice(PackIns packIns, Map<String, Object> vals)
    {
        Map<String, Object> r = new HashMap<>();

        if (packIns.getPriceType() == PackIns.PRICE_FIXED)
        {
            r.put("total", packIns.getPrice());
            r.put("premium", packIns.getPrice()); //兼容
        }
        else if (packIns.getPriceType() == PackIns.PRICE_FACTORS)
        {
            StringBuffer key = new StringBuffer();

            if (packIns.getPrice() != null) for (String f : (String[])packIns.getPrice())
            {
                Object val = vals.get(f);

                if (val == null)
                {
                    for (InputField field : packIns.getInputForm())
                        if (field.getVar().equals(f))
                            val = field.getValue();
                }

                if (val == null)
                {
                    key = null;
                    break;
                }

                key.append(val.toString());
                key.append(",");
            }

            Double rate = saleDao.getPackRate(packIns, Common.isEmpty(key) ? null : key.toString());
            if (rate != null)
            {
                Double total = rate * Common.doubleOf(vals.get("QUANTITY"), 1);
                r.put("total", total);
                r.put("premium", total); //兼容
            }
        }
        else if (packIns.getPriceType() == PackIns.PRICE_PLAN)
        {
            JSONObject json = new JSONObject();
            json.put("planId", packIns.getPrice());
            json.put("with", Lifeins.translate(packIns, vals));

            JSONObject res = serviceMgr.req("lifeins", "plan/perform.json", json).getJSONObject("content");
            r.put("total", res.get("premium"));
        }
        else if (packIns.getPriceType() == PackIns.PRICE_LIFE)
        {
            JSONObject json = new JSONObject();
            json.put("scriptId", packIns.getPrice());
            json.put("opt", "try");
            json.put("with", Lifeins.translate(packIns, vals));

            return serviceMgr.req("lifeins", "perform.json", json).getJSONObject("content");
        }
        else if (packIns.getPriceType() == PackIns.PRICE_PRODUCT)
        {
            JSONObject json = new JSONObject();
            json.put("productId", packIns.getPrice());
            json.put("opt", "try");
            json.put("with", Lifeins.translate(packIns, vals));

            return serviceMgr.req("lifeins", "perform.json", json).getJSONObject("content");
        }

        return r;
    }

}
