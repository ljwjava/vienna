package lerrain.service.lifeins.plan.format;

import lerrain.project.insurance.product.attachment.AttachmentParser;
import lerrain.project.insurance.product.load.XmlNode;
import lerrain.tool.formula.Formula;
import lerrain.tool.formula.FormulaUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by lerrain on 2017/11/9.
 */
public class FGraphParser implements AttachmentParser, Serializable
{
    @Override
    public String getName()
    {
        return "fgraph";
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
        Map items = new HashMap();

        for (Iterator iter = e.getChildren("item").iterator(); iter.hasNext(); )
        {
            XmlNode n1 = (XmlNode)iter.next();

            Formula f = FormulaUtil.formulaOf(n1.getText());
            items.put(n1.getAttribute("name"), f);
        }

        return items;
    }
}
