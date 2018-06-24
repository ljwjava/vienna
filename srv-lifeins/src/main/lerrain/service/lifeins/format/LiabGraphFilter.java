package lerrain.service.lifeins.format;

import lerrain.project.insurance.plan.Commodity;
import lerrain.project.insurance.plan.Plan;
import lerrain.project.insurance.plan.filter.FilterPlan;
import lerrain.project.insurance.product.Company;
import lerrain.project.insurance.product.attachment.AttachmentParser;
import lerrain.project.insurance.product.load.XmlNode;
import lerrain.tool.Common;
import lerrain.tool.formula.Formula;
import lerrain.tool.formula.FormulaUtil;
import lerrain.tool.formula.Value;
import lerrain.tool.script.Script;
import lerrain.tool.script.Stack;

import java.io.Serializable;
import java.util.*;

/**
 * Created by lerrain on 2017/11/9.
 */
public class LiabGraphFilter implements FilterPlan
{
    @Override
    public Object filtrate(Plan plan, String attachmentName)
    {
        Company company = plan.getCompany();
        if (company == null)
            return null;

        List<LiabGraphSet> liabs = (List<LiabGraphSet>)company.getAttachment(attachmentName);
        if (liabs == null)
            return null;

        Map<String, Object> vals = new HashMap<>();
        int count = 0;

        for (int i = 0; i < plan.size(); i++)
        {
            Commodity c = plan.getCommodity(i);
            List<LiabGraphItem> list = (List<LiabGraphItem>)c.getProduct().getAttachment(attachmentName);
            if (list != null)
            {
                for (LiabGraphItem lgi : list)
                {
                    String mode = lgi.getMode();
                    String type = lgi.getType();
                    String text = Value.stringOf(lgi.getText(), c.getFactors());
                    Object val = lgi.getValue() == null ? null : lgi.getValue().run(c.getFactors());

                    if (mode == null)
                        mode = "#" + count++;

                    List items = (List)vals.get(type + "/" + mode);
                    if (items == null)
                    {
                        items = new ArrayList();
                        vals.put(type + "/" + mode, items);
                    }

                    Map tt = new HashMap();
                    tt.put("productName", c.getProduct().getName());
                    tt.put("productAbbrName", c.getProduct().getAbbrName());
                    tt.put("vendor", c.getProduct().getCompany().getId());
                    tt.put("text", text);

                    items.add(new Object[]{val, tt});
                }
            }
        }

        List res = new ArrayList();

        Stack stack = new Stack();
        for (LiabGraphSet lgs : liabs)
        {
            Map lset = new HashMap<>();
            lset.put("name", lgs.getName());

            List lgsList = new ArrayList();
            for (LiabGraphSet.LiabGraphType lgt : lgs.toList())
            {
                Map ltype = new HashMap();
                ltype.put("name", lgt.getName());

                String type = lgt.getType();

                List lgtList = new ArrayList();
                for (Map.Entry<String, ScriptText> e : lgt.getContent().entrySet())
                {
                    Map litem = new HashMap();
                    String mode = e.getKey();

                    Object total = null;
                    List texts = new ArrayList();

                    List<Object[]> items = (List<Object[]>)vals.get(type + "/" + mode);
                    if (items == null || items.isEmpty())
                        continue;

                    for (Object[] item : items)
                    {
                        texts.add(item[1]);
                        total = total == null ? item[0] : doubleOf(total) + doubleOf(item[0]);
                    }

                    stack.set("self", total);
                    String text = Value.stringOf(e.getValue(), stack);

                    litem.put("text", text);
                    litem.put("detail", texts);

                    lgtList.add(litem);
                }

                for (Map.Entry<String, Object> e : vals.entrySet())
                {
                    String key = e.getKey();
                    if (key.startsWith(type + "/#"))
                    {
                        List<Object[]> val = (List<Object[]>)e.getValue();

                        Map litem = new HashMap();
                        litem.put("text", val.get(0)[1]);

                        lgtList.add(litem);
                    }
                }

                if (!lgtList.isEmpty())
                {
                    ltype.put("detail", lgtList);
                    lgsList.add(ltype);
                }
            }

            if (!lgsList.isEmpty())
            {
                lset.put("detail", lgsList);
                res.add(lset);
            }
        }

        return res;
    }

    private double doubleOf(Object val)
    {
        if (val == null)
        {
            return 0;
        }
        else if (val instanceof Number)
        {
            return ((Number)val).doubleValue();
        }
        else
        {
            try
            {
                return Double.parseDouble(val.toString());
            }
            catch (Exception e)
            {
                return 0;
            }
        }
    }

}
