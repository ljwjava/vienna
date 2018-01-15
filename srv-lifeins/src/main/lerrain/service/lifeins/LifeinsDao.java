package lerrain.service.lifeins;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.project.insurance.plan.Commodity;
import lerrain.project.insurance.plan.Plan;
import lerrain.project.insurance.product.*;
import lerrain.project.insurance.product.load.XmlNode;
import lerrain.service.common.Log;
import lerrain.tool.Common;
import lerrain.tool.formula.Formula;
import lerrain.tool.formula.FormulaUtil;
import lerrain.tool.script.Script;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class LifeinsDao
{
    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    LifeinsService lifeins;

    public void loadPacks()
    {
        jdbc.query("select * from t_ins_pack where valid is null", new RowCallbackHandler()
        {
            @Override
            public void processRow(ResultSet rs) throws SQLException
            {
                Company company = lifeins.getCompany(rs.getString("company"));

                JSONObject init = JSON.parseObject(rs.getString("init"));
                JSONArray detail = JSON.parseArray(rs.getString("detail"));

                Insurance ins = new Insurance();
                ins.setCompany(company);

                ins.setType(Insurance.PACKAGE);
                ins.setSequence(Common.intOf(rs.getObject("sequence"), 1000));
                ins.setId(rs.getString("product_id"));
                ins.setName(rs.getString("name"));
                ins.setAbbrName(rs.getString("name_abbr"));
                ins.setUnit(Common.intOf(rs.getObject("unit"), 1000));
                ins.getPurchase().setPremium(Script.scriptOf("PremiumChildren"));

                String purchaseTypeStr = rs.getString("purchase");
                ins.getPurchase().setPurchaseType(
                    "none".equals(purchaseTypeStr) ? Purchase.NONE :
                    "quantity".equals(purchaseTypeStr) ? Purchase.QUANTITY :
                    "rank".equals(purchaseTypeStr) ? Purchase.RANK :
                    "rank_and_quantity".equals(purchaseTypeStr) ? Purchase.RANK_AND_QUANTITY :
                        Purchase.AMOUNT);

                InitValue iv = new InitValue();

                for (Map.Entry<String, Object> e : init.entrySet())
                    iv.set(e.getKey(), Script.scriptOf((String)e.getValue()));

                ins.setInitValue(iv);

                Portfolio portfolio = new Portfolio();

                for (int i = 0; i < detail.size(); i++)
                {
                    JSONObject n1 = detail.getJSONObject(i);

                    String productId = n1.getString("clauseId");
                    String parentId = n1.getString("parentId");
                    String c = n1.getString("condition");

                    InitValue civ = null;

                    if (n1.containsKey("factors"))
                    {
                        civ = new InitValue();
                        for (Map.Entry<String, Object> e : n1.getJSONObject("factors").entrySet())
                            civ.set(e.getKey(), Script.scriptOf((String) e.getValue()));
                    }

                    portfolio.addProduct(parentId == null ? null : company.getProduct(parentId), company.getProduct(productId), civ, FormulaUtil.formulaOf(c), null);
                }

                ins.setPortfolio(portfolio);

                List<Field> list = loadInput(Common.toLong(rs.getObject("input_id")), ins);
                ins.setAdditional("input", list);

                String[][] keys = new String[][] {{"pay", "PAY"}, {"insure", "INSURE"}};
                if (list != null) for (Field f : list)
                {
                    if (f != null) for (int j = 0; j < keys.length; j++)
                    {
                        if (ins.getOptionList(keys[j][0]) == null && keys[j][1].equals(f.getName()))
                        {
                            Formula fm = f.getOptions();
                            if (fm == null)
                                continue;

                            JSONArray listArr = (JSONArray) JSON.toJSON(fm.run(null));
                            for (int i = 0; i < listArr.size(); i++)
                            {
                                JSONArray jo = listArr.getJSONArray(i);
                                ins.addOption(keys[j][0], company.getOption(keys[j][0], jo.getString(0)));
                            }
                        }
                    }
                }

                company.addProduct(ins);
            }
        });
    }

    public void supplyClauses()
    {
        jdbc.query("select * from t_product where type = 1 and refer_id is not null", new RowCallbackHandler()
        {
            @Override
            public void processRow(ResultSet rs) throws SQLException
            {
                String clauseId = rs.getString("refer_id");
                Insurance ins = lifeins.getProduct(clauseId);

                if (ins != null)
                    ins.setAdditional("input", loadInput(Common.toLong(rs.getObject("input_id")), ins));
                else
                    Log.alert(clauseId + " is not found.");
            }
        });
    }

    private List<Field> loadInput(Long inputId, final Insurance ins)
    {
        if (inputId == null)
            return null;

       return jdbc.query("select * from t_input where input_id = ? order by seq", new RowMapper<Field>()
        {
            @Override
            public Field mapRow(ResultSet m, int rowNum) throws SQLException
            {
                String scope = m.getString("scope");
                if (scope == null)
                    return null;
                if ((!ins.isRider() && scope.indexOf("primary") < 0) && (scope.indexOf(ins.getId()) < 0))
                    return null;

                Field c = new Field();
                c.setName(m.getString("name"));
                c.setLabel(m.getString("label"));
                c.setType(m.getString("type"));
                c.setWidget(m.getString("widget"));
                c.setValue(LifeinsUtil.translate(c.getType(), m.getString("value")));

                Formula f = Script.scriptOf(m.getString("detail"));
                if (f != null)
                    c.setOptions(f);

                return c;
            }
        }, inputId);
    }
}
