package lerrain.service.lifeins;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.project.insurance.plan.Commodity;
import lerrain.project.insurance.plan.Plan;
import lerrain.project.insurance.plan.UnstableList;
import lerrain.project.insurance.plan.filter.StaticText;
import lerrain.project.insurance.plan.filter.chart.Chart;
import lerrain.project.insurance.plan.filter.chart.ChartLine;
import lerrain.project.insurance.plan.filter.liability.Liability;
import lerrain.project.insurance.plan.filter.table.Blank;
import lerrain.project.insurance.plan.filter.table.Table;
import lerrain.project.insurance.product.attachment.coverage.Coverage;
import lerrain.project.insurance.product.attachment.coverage.CoverageParagraph;
import lerrain.service.lifeins.format.FGraphFilter;
import lerrain.tool.Common;
import lerrain.tool.formula.Formula;
import lerrain.tool.script.Script;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LifeinsShow
{
    static Formula f = Script.scriptOf("if (IT == null || IT.CSV == null) { return null }; var l = new list(); for (var I=0;I<INSURE_PERIOD;I++) { l += IT.CSV(I); } return l;");

    public static Object formatRadarGraph(Plan plan)
    {
        FGraphFilter fgf = new FGraphFilter();
        return fgf.filtrate(plan, "fgraph");
    }

    public static Map formatValChart(Plan plan)
    {
        Map r = new HashMap();

        List res = new ArrayList();
        for (int i=0;i<plan.size();i++)
        {
            Commodity c = plan.getCommodity(i);
            List list = (List)f.run(c.getFactors());
            if (list != null)
            {
                addUpCsv(res, list);
            }
            else
            {
                UnstableList children = c.getChildren();
                for (int k = 0; children != null && k < children.size(); k++)
                    addUpCsv(res, (List)f.run(children.get(k).getFactors()));
            }
        }

        r.put("age", plan.getFactor("AGE"));
        r.put("csv", res);

        return r;
    }

    private static void addUpCsv(List res, List list)
    {
        if (list != null)
        {
            for (int j=0;j<list.size();j++)
            {
                if (res.size() <= j)
                    res.add(list.get(j));
                else
                    res.set(j, Common.doubleOf(res.get(j), 0) + Common.doubleOf(list.get(j), 0));
            }
        }
    }

    public static Object formatBenefit(Plan plan)
    {
        return plan.format("benefit");
    }

    public static JSONArray formatTable(Plan plan)
    {
        JSONArray r = new JSONArray();
        return r;
    }

    public static JSONArray formatLiability(Commodity c)
    {
        Liability coverage = (Liability)c.format("liability");

        JSONArray l2 = new JSONArray();

        for (int k=0;k<coverage.size();k++)
        {
            Liability cp = coverage.getParagraph(k);

            JSONObject m3 = new JSONObject();
            m3.put("title", cp.getTitle());

            JSONArray l3 = new JSONArray();
            m3.put("content", l3);

            for (int l=0;l<cp.size();l++)
            {
                Liability cc = cp.getParagraph(l);
                if (cc.getContent() instanceof String)
                {
                    JSONObject m4 = new JSONObject();
                    m4.put("text", cc.getContent());
                    m4.put("type", "text");

                    l3.add(m4);
                }
                else if (cc.getContent() instanceof Table)
                {
                    l3.add(tableOf((Table)cc.getContent(), false));
                }
            }

            l2.add(m3);
        }

        return l2;
    }

    public static JSONArray format(Plan plan, String style)
    {
        JSONArray r = new JSONArray();
        for (int i=0;i<plan.size();i++)
        {
            Commodity c = plan.getCommodity(i);

            JSONObject json = new JSONObject();
            json.put("productId", c.getProduct().getId());
            json.put("productAbbrName", c.getProduct().getAbbrName());
            json.put("productName", c.getProduct().getName());

            Object content = null;
            if ("coverage".equals(style))
                content = formatCoverage(c);
            else if ("chart".equals(style))
                content = formatChart(c);

            json.put("content", content);
            r.add(json);
        }

        return r;
    }

    public static JSONArray formatCoverage(Commodity c)
    {
        if (!c.hasFormat("coverage"))
            return null;

        Coverage coverage = (Coverage)c.format("coverage");

        JSONArray l2 = new JSONArray();

        for (int k=0;k<coverage.getParagraphCount();k++)
        {
            CoverageParagraph cp = coverage.getParagraph(k);

            JSONObject m3 = new JSONObject();
            m3.put("title", cp.getTitle());

            JSONArray l3 = new JSONArray();
            m3.put("content", l3);

            for (int l=0;l<cp.size();l++)
            {
                if (cp.getType(l) == CoverageParagraph.TEXT)
                {
                    JSONObject m4 = new JSONObject();
                    m4.put("text", cp.getContent(l));
                    m4.put("type", "text");

                    l3.add(m4);
                }
                else if (cp.getType(l) == CoverageParagraph.TABLE)
                {
                    l3.add(tableOf((Table)cp.getContent(l), false));
                }
            }

            l2.add(m3);
        }

        return l2;
    }

    public static JSONObject formatChart(Commodity c)
    {
        if (c.getProduct().isRider())
            return null;

        List<Chart> list = new ArrayList<>();

        if (c.hasFormat("benefit_chart"))
            list.addAll((List<Chart>)c.format("benefit_chart"));

        for (int i = 0; i < c.getPlan().size(); i++)
        {
            Commodity rider = c.getPlan().getCommodity(i);
            if (rider.getParent() == c && rider.hasFormat("benefit_chart"))
                list.addAll((List<Chart>)rider.format("benefit_chart"));
        }

        int age = Common.intOf(c.getFactor("AGE"), 0);

        Map<String, double[]> temp = new HashMap();
        List s1 = new ArrayList<>();

        for (Chart chart : list)
        {
            int b = chart.getStart();
            int e = chart.getEnd();

            for (int i = 0; i < chart.size(); i++)
            {
                ChartLine line = chart.getLine(i);

                double[] v = temp.get(line.getName());
                if (v == null)
                {
                    v = new double[e - b + 1];

                    Map m = new HashMap<>();
                    m.put("name", line.getName());
                    m.put("type", line.getType());
                    String color = Integer.toHexString(line.getColor() | 0xFF000000);
                    m.put("color", color.substring(2));
                    m.put("data", v);
                    s1.add(m);

                    temp.put(line.getName(), v);
                }

                //主险的年期总是大于等于附加险的年期，所以不需要判断v的长度
                for (int j = b; j <= e; j++)
                    v[j - b] += line.getData()[j];
            }
        }

        JSONObject r = new JSONObject();
        r.put("name",  c.getProduct().getName());
        r.put("age", age);
        r.put("data", s1);	// value

        return r;
    }


    public static JSONArray formatTable(Commodity c, boolean fold)
    {
        JSONArray r = new JSONArray();

        List<Object> list = (List)c.format("benefit_table");
        if (list != null) for (Object line : list)
        {
            if (line instanceof Table)
            {
                r.add(tableOf((Table)line, fold));
            }
            else if (line instanceof StaticText)
            {
                StaticText st = (StaticText)line;
                Map json = new HashMap();
                json.put("text", st.getText());
                json.put("bold", st.getStyle("bold"));
                r.add(json);
            }
        }

        return r;
    }

    private static JSONObject tableOf(Table table, boolean fold)
    {
        Map<String, String>[][] h = new Map[table.getTitleHeight()][table.getMaxCol()];
        for (int i=0;i<table.getTitleHeight();i++)
        {
            for (int j=0;j<table.getMaxCol();j++)
            {
                Blank blank = table.getTitleBlank(i, j);
                if (blank != null && blank.getText() != null)
                {
                    h[i][j] = new HashMap<String, String>();
                    h[i][j].put("row", blank.getRowspan() + "");
                    h[i][j].put("col", blank.getColspan() + "");
                    h[i][j].put("text", blank.getText());
                }
            }
        }

        List<String[]> m = new ArrayList<>();
        for (int i=0;i<table.getMaxRow();i++)
        {
            if (fold && i >= 10 && i % 5 != 4 && i != table.getMaxRow() - 1)
                continue;

            String[] mm = new String[table.getMaxCol()];
            for (int j=0;j<table.getMaxCol();j++)
            {
                Blank blank = table.getBlank(i, j);
                if (blank != null)
                {
                    mm[j] = blank.getText();
                }
            }
            m.add(mm);
        }

        JSONObject json = new JSONObject();
        json.put("head", h);
        json.put("body", m);
        json.put("type", "table");
        json.put("name", table.getName());
        json.put("desc", table.getAdditional("desc"));

        return json;
    }
}
