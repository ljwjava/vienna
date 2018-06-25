package lerrain.service.varia;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AgentFetchDao
{
	@Autowired
	JdbcTemplate jdbc;

	public List<Map> find1(String certNo, String name)
	{
		try
		{
			List<Map> m = jdbc.query("select * from t_agent_1 where cert_no = ? and `name` = ? and flag = 1", new RowMapper<Map>()
			{
				@Override
				public Map mapRow(ResultSet tc, int arg1) throws SQLException
				{
					Map m = new HashMap();

					m.put("name", tc.getString("name"));
					m.put("gender", tc.getString("gender"));
					m.put("certNo", tc.getString("cert_no"));
					m.put("certfiNo", tc.getString("certfi_no"));
					m.put("certfiStatus", tc.getString("certfi_status"));
					m.put("bizNo", tc.getString("biz_no"));
					m.put("bizStatus", tc.getString("biz_status"));
					m.put("validDate", tc.getString("valid_date"));
					m.put("bizScope", tc.getString("biz_scope"));
					m.put("bizArea", tc.getString("biz_area"));
					m.put("company", tc.getString("company"));

					return m;
				}
			}, certNo, name);

			return m.isEmpty() ? null : m;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public List<Map> find2(String certNo, String name)
	{
		try
		{
			List<Map> m = jdbc.query("select * from t_agent_2 where cert_no = ? and `name` = ? and flag = 1", new RowMapper<Map>()
			{
				@Override
				public Map mapRow(ResultSet tc, int arg1) throws SQLException
				{
					Map m = new HashMap();

					m.put("name", tc.getString("name"));
					m.put("gender", tc.getString("gender"));
					m.put("certNo", tc.getString("cert_no"));
					m.put("certfiNo", tc.getString("certfi_no"));
					m.put("certfiType", tc.getString("certfi_type"));
					m.put("certfiScope", tc.getString("certfi_scope"));
					m.put("certfiValidDate", tc.getString("certfi_valid_date"));

					return m;
				}
			}, certNo, name);

			return m.isEmpty() ? null : m;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public void save1(List<Map> xx, int flag)
	{
		for (Map m : xx)
		{
			jdbc.update("insert into t_agent_1(`name`, gender, cert_no, certfi_no, certfi_status, biz_no, biz_status, valid_date, biz_scope, biz_area, company, flag, `time`, file) values(?,?,?,?,?,?,?,?,?,?,?,?,now(),?)",
					m.get("name"),
					m.get("gender"),
					m.get("certNo"),
					m.get("certfiNo"),
					m.get("certfiStatus"),
					m.get("bizNo"),
					m.get("bizStatus"),
					m.get("validDate"),
					m.get("bizScope"),
					m.get("bizArea"),
					m.get("company"),
					flag,
					m.get("file")
			);
		}
	}

	public void save2(List<Map> xx, int flag)
	{
		for (Map m : xx)
		{
			jdbc.update("insert into t_agent_2(`name`, gender, cert_no, certfi_no, certfi_type, certfi_scope, certfi_valid_date, flag, `time`, file) values(?,?,?,?,?,?,?,?,now(),?)",
					m.get("name"),
					m.get("gender"),
					m.get("certNo"),
					m.get("certfiNo"),
					m.get("certfiType"),
					m.get("certfiScope"),
					m.get("certfiValidDate"),
					flag,
					m.get("file")
			);
		}
	}
}
