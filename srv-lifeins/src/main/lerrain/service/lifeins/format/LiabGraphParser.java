package lerrain.service.lifeins.format;

import lerrain.project.insurance.product.attachment.AttachmentParser;
import lerrain.project.insurance.product.load.XmlNode;
import lerrain.tool.Common;
import lerrain.tool.formula.Formula;
import lerrain.tool.formula.FormulaUtil;
import lerrain.tool.script.Script;

import java.io.Serializable;
import java.util.*;

/**
 * Created by lerrain on 2017/11/9.
 */
public class LiabGraphParser implements AttachmentParser, Serializable
{
    @Override
    public String getName()
    {
        return "liab_graph";
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
        List list = new ArrayList();

        for (Iterator iter = e.getChildren().iterator(); iter.hasNext(); )
        {
            XmlNode n1 = (XmlNode)iter.next();

            if ("set".equals(n1.getName()))
            {
                LiabGraphSet lgs = new LiabGraphSet(n1.getAttribute("name"));

                for (Iterator iter2 = n1.getChildren().iterator(); iter2.hasNext(); )
                {
                    XmlNode n2 = (XmlNode)iter2.next();
                    LiabGraphSet.LiabGraphType lgt = lgs.newType(n2.getAttribute("type"), n2.getAttribute("name"));

                    for (Iterator iter3 = n2.getChildren().iterator(); iter3.hasNext(); )
                    {
                        XmlNode n3 = (XmlNode)iter3.next();
                        lgt.addMode(n3.getAttribute("code"), new ScriptText(n3.getText()));
                    }
                }

                list.add(lgs);
            }
            else if ("item".equals(n1.getName()))
            {
                LiabGraphItem item = new LiabGraphItem();
                item.setType(n1.getAttribute("type"));
                item.setMode(n1.getAttribute("mode"));
                item.setValue(Script.scriptOf(n1.getAttribute("value")));

                String text = n1.getText();
                if (!Common.isBlank(text))
                    item.setText(new ScriptText(text));

                list.add(item);
            }
        }

        return list;
    }
}
