package lerrain.service.sale.manage;

import lerrain.service.sale.CmsDefine;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class ManageDao
{
    @Autowired
    JdbcTemplate jdbc;

    public List<String[]> loadCompanyList()
    {
        return jdbc.query("select * from t_company", new RowMapper<String[]>()
        {
            @Override
            public String[] mapRow(ResultSet rs, int rowNum) throws SQLException
            {
                String vendorId = rs.getString("id");
                String name = rs.getString("name");

                return new String[] {vendorId, name};
            }
        });
    }

    public Product loadProduct(Long productId)
    {
        return jdbc.queryForObject("select * from t_product where id = ?", new RowMapper<Product>()
        {
            @Override
            public Product mapRow(ResultSet rs, int rowNum) throws SQLException
            {
                Product m = new Product();
                m.setId(rs.getLong("id"));
                m.setCode(rs.getString("code"));
                m.setName(rs.getString("name"));
                m.setCompanyId(rs.getLong("company_id"));

                return m;
            }
        }, productId);
    }

    public List<Product> list(String search, int from, int number)
    {
        StringBuffer sql = new StringBuffer("select a.*, b.name as company_name from t_product a left join t_company b on a.company_id = b.id where a.valid is null");

        if (!Common.isEmpty(search))
            sql.append(" and name like '%" + search + "%' ");
        sql.append(" order by a.id desc limit " + from + ", " + number);

        return jdbc.query(sql.toString(), new RowMapper<Product>()
        {
            @Override
            public Product mapRow(ResultSet rs, int arg1) throws SQLException
            {
                Product m = new Product();
                m.setId(rs.getLong("id"));
                m.setCode(rs.getString("code"));
                m.setName(rs.getString("name"));
                m.setCompanyId(rs.getLong("company_id"));
                m.setCompanyName(rs.getString("company_name"));

                return m;
            }
        });
    }

    public int count(String search)
    {
        StringBuffer sql = new StringBuffer("select count(*) from t_product where valid is null");

        if (!Common.isEmpty(search))
            sql.append(" and name like '%" + search + "%' ");

        return jdbc.queryForObject(sql.toString(), Integer.class);
    }
}
