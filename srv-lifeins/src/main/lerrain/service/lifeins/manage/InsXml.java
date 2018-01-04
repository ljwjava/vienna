package lerrain.service.lifeins.manage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.w3c.dom.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class InsXml
{
    File file;

    Xml xml;

    Xml root;

    Xml param, data, init, interest, duty, attachment, rule, rider;

    List<InsPart> list = new ArrayList();

    public InsXml(File f)
    {
        this.file = f;

        if (f.isFile())
        {
            xml = Xml.nodeOf(f);

            if ("declare".equals(xml.getName()))
                analyse();
        }
        else
        {
            xml = new Xml("declare");
            root = new Xml("product");
            xml.addChild(root);

            analyse();
        }
    }

    private void analyse()
    {
        root = xml.firstChild();

        if (!"product".equals(root.getName()))
            return;

        list.add(new BaseXml(xml));
        list.add(new ParamXml(xml.firstChild("param")));
        list.add(new DataXml(xml.firstChild("data")));
        list.add(new FixedXml(xml,"attachment", "展示"));
    }

    public String getId()
    {
        return root.getAttribute("id");
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
