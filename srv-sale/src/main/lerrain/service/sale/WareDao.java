package lerrain.service.sale;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class WareDao
{
    @Autowired
    JdbcTemplate   jdbc;

    public List<Ware> loadAll()
    {
        return jdbc.query("select * from t_ware", new RowMapper<Ware>()
        {
            @Override
            public Ware mapRow(ResultSet m, int rowNum) throws SQLException
            {
                Ware c = new Ware();
                c.setId(m.getLong("id"));
                c.setName(m.getString("name"));
                c.setCode(m.getString("code"));
                c.setAbbrName(m.getString("abbr_name"));
                c.setType(m.getInt("type"));
                c.setTag(m.getString("tag"));
                c.setPrice(m.getString("price"));
                c.setLogo(m.getString("logo"));
                c.setRemark(m.getString("remark"));

                String banner = m.getString("banner");
                if (banner != null)
                    c.setBanner(banner.split(","));

                String detail = m.getString("detail");
                if (detail != null)
                    c.setDetail(JSON.parseArray(detail));

                return c;
            }
        });
    }

    public List<Long> find(Long platformId)
    {
        return jdbc.queryForList("select ware_id from t_platform_ware where platform_id = ?", Long.class, platformId);
    }

//    public Map loadVendor(Object vendorId)
//    {
//        return jdbc.queryForMap("select id, code, name, logo from t_company where id = ?", vendorId);
//    }
}
