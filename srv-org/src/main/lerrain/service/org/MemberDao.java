package lerrain.service.org;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lerrain.tool.Common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class MemberDao
{
	@Autowired
	JdbcTemplate jdbc;

	public Member loadMember(Long memberId)
	{
		return jdbc.queryForObject("select * from t_member where id = ?", new RowMapper<Member>()
		{
			@Override
			public Member mapRow(ResultSet tc, int arg1) throws SQLException
			{
				Member member = new Member();
				member.setId(tc.getLong("id"));
				member.setName(tc.getString("name"));
				member.setOrgId(tc.getLong("org_id"));
				member.setCompanyId(tc.getLong("company_id"));
				//member.setUserId(Common.toLong(tc.getObject("user_id")));

				return member;
			}
		}, memberId);
	}

	public Member findMember(Long userId)
	{
        return jdbc.queryForObject("select * from t_member where id = ?", new RowMapper<Member>()
		{
			@Override
			public Member mapRow(ResultSet tc, int arg1) throws SQLException
			{
				Member member = new Member();
				member.setId(tc.getLong("id"));
				member.setName(tc.getString("name"));
				member.setOrgId(tc.getLong("org_id"));
				member.setCompanyId(tc.getLong("company_id"));
				member.setUserId(Common.toLong(tc.getObject("user_id")));

				return member;
			}
		}, userId);
	}

    public boolean update(Member member) {
        return jdbc.update("update t_member set name=? where id = ?", member.getName(), member.getId()) > 0;
    }

    public boolean remove(Long id) {
        return jdbc.update("update t_member set valid = 'N' where id = ?", id) > 0;
    }

    public int save(Member member) {
        return jdbc
                .update("insert into t_member (id, name, mobile, status, org_id,company_id,create_time,update_time) values (?,?,?,?,?,?,now(),now())",
                        member.getId(), member.getName(), member.getMobile(), member.getStatus(), member.getOrgId(),
                        member.getCompanyId());
    }
    
    /**
     * 获取上级信息
     * 
     * @param companyId
     * @return
     */
    public List<Enterprise> querySuperior(Long companyId) {
        List<Enterprise> list = new ArrayList<Enterprise>();
        Enterprise result = jdbc.queryForObject("select * from t_member where id = ?", new RowMapper<Enterprise>() {
            @Override
            public Enterprise mapRow(ResultSet tc, int arg1) throws SQLException {
                Enterprise enterprise = new Enterprise();
                enterprise.setId(tc.getLong("id"));
                enterprise.setCompanyName(tc.getString("company_name"));
                enterprise.setCompanyType(tc.getString("company_type"));
                enterprise.setParentId(tc.getLong("parent_id"));
                enterprise.setTelephone(tc.getString("telephone"));
                enterprise.setEmail(tc.getString("email"));
                enterprise.setIsDeleted(tc.getString("is_deleted"));
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
        List<Enterprise> list = jdbc.queryForList("select * from t_member where id =" + parentId, Enterprise.class);
        Enterprise result = list.get(0);
        result.setLevel(level+1);
        resultList.add(result);
        if(result.getParentId() != null ) {
            querySuperiorByParentId(result.getParentId(), result.getLevel(), resultList);
        }
    }

    public Long countByOrgId(Long orgId) {
        return jdbc.queryForObject("select count(*) as total from t_member where org_id=? and valid is null",
                new RowMapper<Long>() {
            @Override
                    public Long mapRow(ResultSet tc, int arg1) throws SQLException {
                        Long result = tc.getLong("total");
                        return result;
            }
                }, orgId);
    }

    public List<Map<String, Object>> listByOrgId(Long orgId, int from, int number, String searchCondition) {
        List<Map<String, Object>> list = null;
        if (StringUtils.isEmpty(searchCondition)) {
            list = jdbc.queryForList("select * from t_member where org_id=? and valid is null limit ?, ?", orgId, from,
                    number);
        } else {
            list = jdbc
                    .queryForList(
"select * from t_member where org_id=? and (name like '%" + searchCondition
                    + "%' or mobile like '%" + searchCondition + "%') and valid is null limit ?, ?",
 orgId, from,
                    number);
        }
        if (list != null && list.size() > 0) {
            for (Map<String, Object> map : list) {
                Map map1 = jdbc.queryForMap("select status from t_user where user_id=?", map.get("id"));
                map.put("userStatus", map1.get("status"));
            }
        }
        return list;
    }

}
