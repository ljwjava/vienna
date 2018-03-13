package lerrain.service.org;

import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

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
		return jdbc.queryForObject("select * from t_member where user_id = ?", new RowMapper<Member>()
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

}
