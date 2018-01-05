package lerrain.service.lifeins.manage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.project.insurance.product.Company;
import org.w3c.dom.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InsXml
{
    File file;

    Xml xml;

    Xml root;

    Company company;

    List<InsPart> list = new ArrayList();

    public InsXml(File f, Map<String, Company> allc)
    {
        this.file = f;

        if (f.isFile())
        {
            xml = Xml.nodeOf(f);

            if ("declare".equals(xml.getName()))
                analyse(allc);
        }
        else
        {
            xml = new Xml("declare");
            root = new Xml("product");
            xml.addChild(root);

            analyse(allc);
        }
    }

    private void analyse(Map<String, Company> allc)
    {
        root = xml.firstChild();

        if (!"product".equals(root.getName()))
            return;

        company = allc.get(getCompanyId());

        list.add(new BaseXml(root));
        list.add(new ParamXml(root.firstChild("param"), company));
        list.add(new DataXml(root.firstChild("data")));
        list.add(new RiderXml(root.firstChild("rider"), company));
        list.add(new RuleXml(root.firstChild("rule")));
        list.add(new FixedXml(root,"attachment", "展示"));
    }

    public String getId()
    {
        return root.getAttribute("id");
    }

    public String getCompanyId()
    {
        return root.getAttribute("corporation_id");
    }

    public boolean isInsurance()
    {
        return root != null && "product".equals(root.getName());
    }

    public JSONObject toJson()
    {
        JSONObject r = new JSONObject();

        for (InsPart ip : list)
        {
            JSONObject json = ip.toJson();
            r.put(ip.getCode(), json);
        }

        return r;
    }

    public void save(JSONObject form)
    {
        for (InsPart ip : list)
        {
            ip.save(form.getJSONObject(ip.getCode()));
        }
    }

    public String toString()
    {
        return xml.toString();
    }
}
