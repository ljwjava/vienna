package lerrain.service.lifeins.manage;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.project.insurance.product.Company;
import lerrain.project.insurance.product.Insurance;

import java.util.ArrayList;
import java.util.List;

public class RiderXml implements InsPart
{
    Xml xml;

    JSONArray ja = new JSONArray();

    public RiderXml(Xml xml, Company c)
    {
        this.xml = xml;

        List<Insurance> list = c.getProductList();
        for (Insurance ins : list)
        {
            if (ins != null && ins.isRider())
            {
                ja.add(new String[] {ins.getId(), ins.getName()});
            }
        }
    }

    @Override
    public JSONObject toJson()
    {
        JSONObject r = new JSONObject();
        r.put("name", "附加险");
        r.put("code", getCode());

        JSONObject riders = new JSONObject();
        for (Xml x : xml.getChildren())
        {
            riders.put(x.getAttribute("id"), true);
        }

        List<FormField> form = new ArrayList();
        form.add(FormField.fieldOf("rider", "可选附加险", "multiswt", ja, riders, 3));

        r.put("form", form);

        return r;
    }

    @Override
    public Xml save(JSONObject form)
    {
        return xml;
    }

    @Override
    public String getCode()
    {
        return "rider";
    }
}
