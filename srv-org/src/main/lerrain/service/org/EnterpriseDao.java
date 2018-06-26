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

/**
 * 类EnterpriseDao.java的实现描述：渠道DAO
 * 
 * @author iyb-wangguangrong 2018年5月9日 下午5:00:20
 */
@Repository
public class EnterpriseDao
{
	@Autowired
	JdbcTemplate jdbc;

    /**
     * 获取上级信息
     * 
     * @param companyId
     * @return
     */
    public List<Enterprise> querySuperior(Long companyId) {
        List<Enterprise> list = new ArrayList<Enterprise>();
        Enterprise result = jdbc.queryForObject("select * from t_enterprise where id = ?", new RowMapper<Enterprise>() {
            @Override
            public Enterprise mapRow(ResultSet tc, int arg1) throws SQLException {
                Enterprise enterprise = new Enterprise();
                enterprise.setId(tc.getLong("id"));
                enterprise.setCompanyName(tc.getString("company_name"));
                enterprise.setCompanyType(tc.getString("company_type"));
                enterprise.setParentId(tc.getLong("parent_id"));
                enterprise.setTelephone(tc.getString("telephone"));
                enterprise.setEmail(tc.getString("email"));
                enterprise.setIsDeleted(tc.getString("valid"));
                enterprise.setLevel(0);
                return enterprise;
            }
        }, companyId);
        list.add(result);
        querySuperiorByParentId(result.getParentId(), result.getLevel(), list);
        return list;
    }

    /**
     * 循环获取上级信息
     * 
     * @param parentId
     * @param level
     * @param resultList
     */
    private void querySuperiorByParentId(Long parentId, int level, List<Enterprise> resultList) {
        Enterprise result = jdbc.queryForObject("select * from t_enterprise where id = ?", new RowMapper<Enterprise>() {
            @Override
            public Enterprise mapRow(ResultSet tc, int arg1) throws SQLException {
                Enterprise enterprise = new Enterprise();
                enterprise.setId(tc.getLong("id"));
                enterprise.setCompanyName(tc.getString("company_name"));
                enterprise.setCompanyType(tc.getString("company_type"));
                enterprise.setParentId(tc.getLong("parent_id"));
                enterprise.setTelephone(tc.getString("telephone"));
                enterprise.setEmail(tc.getString("email"));
                enterprise.setIsDeleted(tc.getString("valid"));
                return enterprise;
            }
        }, parentId);        
        result.setLevel(level+1);
        resultList.add(result);
        if(result.getParentId() != null && result.getParentId() > 0) {
            querySuperiorByParentId(result.getParentId(), result.getLevel(), resultList);
        }
    }

    /**
     * 获取下属下级信息
     * 
     * @param companyId
     * @return
     */
    public List<Enterprise> querySubordinate(Long companyId) {
        List<Enterprise> list = new ArrayList<Enterprise>();
        querySubordinateById(companyId, 0, list);
        return list;
    }

    /**
     * 获取树形下级渠道信息
     * 
     * @param companyId
     * @return
     */
    public List<Enterprise> queryAllSubordinate(Long companyId) {
        List<Enterprise> list = new ArrayList<Enterprise>();
        list = jdbc.queryForList("select * from t_enterprise where parent_id = " + companyId, Enterprise.class);
        return list;
    }

    /**
     * 循环获取下级信息
     * 
     * @param parentId
     * @param level
     * @param resultList
     */
    private void querySubordinateById(Long id, int level, List<Enterprise> resultList) {
        List<Enterprise> list = new ArrayList<Enterprise>();
        List<Map<String, Object>> listMap = jdbc
                .queryForList("select id,company_name as companyName,telephone,email, create_time as gmtCreated,parent_id as parentId from t_enterprise where parent_id ="
                        + id);
        if (listMap != null && listMap.size() > 0) {
            for (Map<String, Object> map : listMap) {
            	// 查询用户信息
            	Map<String,Object> userMap = jdbc.queryForMap("select u.user_id,u.status from t_org org ,t_user u where org.id= u.user_id and org.company_id= ? and org.parent_id is null", map.get("id"));
            	if(userMap != null && userMap.size() > 0) {
                	map.put("userId", userMap.get("user_id"));
                	map.put("userStatus", userMap.get("status"));
            	}
                list.add(JSON.parseObject(JSON.toJSONString(map), Enterprise.class));
            }
        }
        if (list != null && list.size() > 0) {
            for (Enterprise enterprise : list) {
                enterprise.setLevel(level + 1);
                resultList.add(enterprise);
                // 查询是否还有下级
                querySubordinateById(enterprise.getId(), enterprise.getLevel(), resultList);
            }
        }
    }

    public int save(Enterprise enterprise) {
        return jdbc
                .update("insert into t_enterprise (id, company_name,telephone,parent_id,email, create_time,update_time) values (?,?,?,?,?,now(),now())",
                        enterprise.getId(), enterprise.getCompanyName(), enterprise.getTelephone(),
                        enterprise.getParentId(),enterprise.getEmail());
    }

    public Enterprise queryById(Long companyId) {
        Enterprise enterprise = jdbc.queryForObject("select * from t_enterprise where id = ?",
                new RowMapper<Enterprise>() {
                    @Override
                    public Enterprise mapRow(ResultSet tc, int arg1) throws SQLException {
                        Enterprise enterprise = new Enterprise();
                        enterprise.setId(tc.getLong("id"));
                        enterprise.setCompanyName(tc.getString("company_name"));
                        enterprise.setCompanyType(tc.getString("company_type"));
                        enterprise.setParentId(tc.getLong("parent_id"));
                        enterprise.setTelephone(tc.getString("telephone"));
                        enterprise.setEmail(tc.getString("email"));
                        enterprise.setIsDeleted(tc.getString("valid"));
                        enterprise.setLevel(0);
                        enterprise.setExtraInfo(tc.getString("extra_info"));
                        return enterprise;
                    }
                }, companyId);
        return enterprise;
    }
    
   public Long getCompanyId(Long userId) {
	   Map<String,Object> userMap = null;
	try {
	   	userMap = jdbc.queryForMap("select org.company_id from t_org org ,t_user u where org.id= u.user_id and u.user_id=?", userId);
	   	if(userMap != null && userMap.size() > 0) {
	       	return Long.valueOf(userMap.get("company_id").toString());
	   	}
	} catch(Exception e) {
	}
	if(userMap == null) {
   		userMap = jdbc.queryForMap("select m.company_id from t_member m ,t_user u where m.id= u.user_id and u.user_id=?", userId);
   	   	if(userMap != null && userMap.size() > 0) {
   	       	return Long.valueOf(userMap.get("company_id").toString());
   	   	}
	}
    return -1l;
   }
   
   public int updateById(Enterprise enterprise) {
       return jdbc
               .update("update t_enterprise set extra_info=?,update_time=now() where id=?",enterprise.getExtraInfo(),
                       enterprise.getId());
   }

}
