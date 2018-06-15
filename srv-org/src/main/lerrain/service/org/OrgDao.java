package lerrain.service.org;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSON;

@Repository
public class OrgDao
{
	@Autowired
	JdbcTemplate jdbc;

	public Org loadOrg(Long orgId)
	{
        return jdbc.queryForObject("select * from t_org where valid is null and id = ?", new RowMapper<Org>()
		{
			@Override
			public Org mapRow(ResultSet tc, int arg1) throws SQLException
			{
				Org org = new Org();
				org.setId(tc.getLong("id"));
				org.setName(tc.getString("name"));
				org.setParentId(tc.getLong("parent_id"));
                org.setCompanyId(tc.getLong("company_id"));
				return org;
			}
		}, orgId);
	}

    /**
     * 获取下级信息
     * 
     * @param parentId
     * @param level
     * @param resultList
     */
    public List<Org> querySubordinateById(Long id) {
        List<Org> list = new ArrayList<Org>();
        List<Map<String, Object>> listMap = jdbc
                .queryForList(
                "select id,name,parent_id as parentId,company_id as companyId from t_org where valid is null and parent_id ="
                        + id);
        if (listMap != null && listMap.size() > 0) {
            for (Map<String, Object> map : listMap) {
                list.add(JSON.parseObject(JSON.toJSONString(map), Org.class));
            }
        }
        return list;
    }

    public boolean update(Org org) {
        return jdbc.update("update t_org set name=? where id = ?", org.getName(), org.getId()) > 0;
    }

    public boolean remove(Long id) {
        return jdbc.update("update t_org set valid = 'N' where id = ?", id) > 0;
    }

    public int save(Org org) {
        return jdbc
                .update("insert into t_org (id, name, type, company_id, parent_id,mobile,create_time,update_time) values (?,?,?,?,?,?,now(),now())",
                        org.getId(), org.getName(), org.getType(), org.getCompanyId(), org.getParentId(),
                        org.getMobile());
    }

    public List<Org> childOrg(Long orgId) {
        List<Org> list = null;
        List<Map<String, Object>> listMap = jdbc
                .queryForList(
                "select id,name,company_id as companyId,mobile,parent_id as parentId from t_org where valid is null and parent_id="
                        + orgId);
        if (listMap != null && listMap.size() > 0) {
            list = new ArrayList<Org>();
            for (Map<String, Object> map : listMap) {
                Org org = JSON.parseObject(JSON.toJSONString(map), Org.class);
                list.add(org);
            }
        }
        return list;
    }

}
