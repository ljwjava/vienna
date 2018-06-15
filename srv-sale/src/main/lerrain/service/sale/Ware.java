package lerrain.service.sale;

import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lerrain on 2017/5/4.
 */
public class Ware
{
    Long id;
    Long vendorId;

    String code;
    String name;
    String abbrName;
    String remark;
    String logo;
    String price;

    JSONArray banner;

    int salesFlag;  // 销售状态（0-在售，1-下架，2-停售）

    List<PackIns> detail;

    public Ware clone() {
        Ware w = new Ware();

        w.setId(id);
        w.setVendorId(vendorId);
        w.setCode(code);
        w.setName(name);
        w.setAbbrName(abbrName);
        w.setRemark(remark);
        w.setLogo(logo);
        w.setPrice(price);
        w.setSalesFlag(salesFlag);
        if(banner != null){
            w.setBanner(JSONArray.parseArray(banner.toJSONString()));
        }

        List<PackIns> dl = null;
        if(detail != null && detail.size() > 0){
            for (PackIns pi : detail) {
                if(dl == null) dl = new ArrayList<PackIns>();
                dl.add(pi);
            }
        }
        w.setDetail(dl);

        return w;
    }

//    int type;

//    String tag;

//    Object detail;

//    public String getTag()
//    {
//        return tag;
//    }
//
//    public void setTag(String tag)
//    {
//        this.tag = "," + tag + ",";
//    }

    public Long getVendorId()
    {
        return vendorId;
    }

    public void setVendorId(Long vendorId)
    {
        this.vendorId = vendorId;
    }

    public List<PackIns> getDetail()
    {
        return detail;
    }

    public void setDetail(List<PackIns> detail)
    {
        this.detail = detail;
    }

//    public boolean match(String tag)
//    {
//        if (tag == null)
//            return true;
//
//        if (this.tag == null)
//            return false;
//
//        return this.tag.indexOf("," + tag + ",") >= 0;
//    }

    public String getPrice()
    {
        return price;
    }

    public void setPrice(String price)
    {
        this.price = price;
    }

    public String getAbbrName()
    {
        return abbrName;
    }

    public void setAbbrName(String abbrName)
    {
        this.abbrName = abbrName;
    }

    public JSONArray getBanner()
    {
        return banner;
    }

    public void setBanner(JSONArray banner)
    {
        this.banner = banner;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

//    public int getType()
//    {
//        return type;
//    }
//
//    public void setType(int type)
//    {
//        this.type = type;
//    }

    public String getLogo()
    {
        return logo;
    }

    public void setLogo(String logo)
    {
        this.logo = logo;
    }

    public int getSalesFlag() {
        return salesFlag;
    }

    public void setSalesFlag(int salesFlag) {
        this.salesFlag = salesFlag;
    }
}
