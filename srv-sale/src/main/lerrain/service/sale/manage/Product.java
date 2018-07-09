package lerrain.service.sale.manage;

import java.util.Map;

public class Product
{
    Long id;
    Long companyId;

    String code;
    String name;

    Map content;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getCompanyId()
    {
        return companyId;
    }

    public void setCompanyId(Long companyId)
    {
        this.companyId = companyId;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Map getContent()
    {
        return content;
    }

    public void setContent(Map content)
    {
        this.content = content;
    }
}
