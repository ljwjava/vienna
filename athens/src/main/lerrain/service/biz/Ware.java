package lerrain.service.biz;

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

    String[] banner;

    int type;

    String tag;

    Object detail;

    public String getTag()
    {
        return tag;
    }

    public void setTag(String tag)
    {
        this.tag = "," + tag + ",";
    }

    public Long getVendorId()
    {
        return vendorId;
    }

    public void setVendorId(Long vendorId)
    {
        this.vendorId = vendorId;
    }

    public Object getDetail()
    {
        return detail;
    }

    public void setDetail(Object detail)
    {
        this.detail = detail;
    }

    public boolean match(String tag)
    {
        if (tag == null)
            return true;

        if (this.tag == null)
            return false;

        return this.tag.indexOf("," + tag + ",") >= 0;
    }

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

    public String[] getBanner()
    {
        return banner;
    }

    public void setBanner(String[] banner)
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

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public String getLogo()
    {
        return logo;
    }

    public void setLogo(String logo)
    {
        this.logo = logo;
    }
}
