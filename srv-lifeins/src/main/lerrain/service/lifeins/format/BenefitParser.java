package lerrain.service.lifeins.format;

import lerrain.project.insurance.product.attachment.AttachmentParser;
import lerrain.project.insurance.product.load.XmlNode;
import lerrain.tool.formula.Formula;
import lerrain.tool.formula.FormulaUtil;
import lerrain.tool.script.Script;

import java.io.Serializable;
import java.util.*;

/**
 * Created by lerrain on 2017/11/9.
 */
public class BenefitParser implements AttachmentParser, Serializable
{
    @Override
    public String getName()
    {
        return "benefit";
    }

    @Override
    public Object parse(Object source, int type)
    {
        Object result = null;

        if (type == AttachmentParser.XML)
        {
            result = prepareXml((XmlNode)source);
        }
        else
        {

        }

        return result;
    }

    private Object prepareXml(XmlNode e)
    {
        List r = new ArrayList();

        for (Iterator iter = e.getChildren("table").iterator(); iter.hasNext(); )
        {
            XmlNode n1 = (XmlNode)iter.next();

            Map t = new HashMap();
            t.put("name", n1.getAttribute("name"));
            t.put("var", n1.getAttribute("var"));

            List r1 = new ArrayList();

            for (Iterator iter2 = n1.getChildren("item").iterator(); iter2.hasNext(); )
            {
                XmlNode n2 = (XmlNode)iter2.next();

                Map m = new HashMap();
                m.put("name", n2.getAttribute("name"));
                m.put("formula", Script.scriptOf(n2.getText()));

                r1.add(m);
            }

            t.put("list", r1);
            r.add(t);
        }

        return r;
    }

}
