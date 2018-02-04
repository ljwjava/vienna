package lerrain.service.customer;

import com.alibaba.fastjson.JSONObject;

import java.util.Date;
import java.util.Map;

public class Customer
{
    Long id;
    Long owner;
    Long platformId;

    String name;
    String gender;

    Date birthday;

    int type;
    int certType;
    String certNo;

    JSONObject detail;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getOwner()
    {
        return owner;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public void setOwner(Long owner)
    {
        this.owner = owner;
    }

    public Long getPlatformId()
    {
        return platformId;
    }

    public void setPlatformId(Long platformId)
    {
        this.platformId = platformId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getGender()
    {
        return gender;
    }

    public void setGender(String gender)
    {
        this.gender = gender;
    }

    public Date getBirthday()
    {
        return birthday;
    }

    public void setBirthday(Date birthday)
    {
        this.birthday = birthday;
    }

    public int getCertType()
    {
        return certType;
    }

    public void setCertType(int certType)
    {
        this.certType = certType;
    }

    public String getCertNo()
    {
        return certNo;
    }

    public void setCertNo(String certNo)
    {
        this.certNo = certNo;
    }

    public JSONObject getDetail()
    {
        return detail;
    }

    public void setDetail(JSONObject detail)
    {
        this.detail = detail;
    }

    public Object get(String key)
    {
        return detail == null ? null : detail.get(key);
    }
}
