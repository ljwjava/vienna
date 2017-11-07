package lerrain.service.data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import lerrain.service.data.function.*;
import lerrain.service.data.source.SourceMgr;
import lerrain.service.data.source.arcturus.ArcMap;
import lerrain.service.data.source.db.DataBase;
import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Formula;
import lerrain.tool.formula.Function;
import lerrain.tool.script.Script;
import lerrain.tool.script.Stack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Repository
public class DataDao
{
	@Autowired JdbcTemplate jdbc;

	@Autowired NextId nextId;
	@Autowired CallService service;
	@Autowired Fold fold;
	@Autowired Unfold unfold;
	@Autowired Parse parse;

	@Autowired SourceMgr sourceMgr;

	DayStr daystr = new DayStr();
	JsonOf jsonOf = new JsonOf();
	Request request = new Request();
	RequestPost post = new RequestPost();

	ModelUtil model = new ModelUtil();
	IybUtil iyb = new IybUtil();

	public Map<Object, Stack> loadAllEnv()
	{
		jdbc.query("select * from t_data_source", new RowCallbackHandler()
		{
			@Override
			public void processRow(ResultSet rs) throws SQLException
			{
				Long id = rs.getLong("id");
				int type = rs.getInt("type");
				Map opts = JSON.parseObject(rs.getString("config"));

				sourceMgr.add(id, type, opts);
			}
		});

		final Map<Object, Stack> map = new HashMap<>();

		jdbc.query("select * from t_env order by id", new RowCallbackHandler()
		{
			@Override
			public void processRow(ResultSet rs) throws SQLException
			{
				Long id = rs.getLong("id");
				Long parentId = Common.toLong(rs.getString("parent_id"));

				Stack env = parentId == null ? new Stack() : new Stack(map.get(parentId));
				fillStack(env, id);
				fillDataSource(env, id);

				map.put(id, env);

				String code = rs.getString("code");
				if (code != null)
					map.put(code, env);
			}
		});

		return map;
	}

//	public Map<String, Formula> loadAllParser()
//	{
//		final Map<String, Formula> m = new HashMap<>();
//
//		jdbc.query("select * from t_data_parser", new RowCallbackHandler()
//		{
//			@Override
//			public void processRow(ResultSet rs) throws SQLException
//			{
//				String type = rs.getString("type");
//
//				try
//				{
//					Formula script = Script.scriptOf(rs.getString("script"));
//					m.put(type, script);
//				}
//				catch (Exception e)
//				{
//					e.printStackTrace();
//				}
//			}
//		});
//
//		return m;
//	}

//	public Map<Long, Operate> loadAllScript()
//	{
//		final Map<Long, Operate> m = new HashMap<>();
//
//		jdbc.query("select * from t_script", new RowCallbackHandler()
//		{
//			@Override
//			public void processRow(ResultSet rs) throws SQLException
//			{
//				Operate opt = new Operate();
//				opt.setId(rs.getLong("id"));
//				opt.setEnvId(rs.getLong("env_id"));
//				opt.setCode(rs.getString("operate"));
//
//				try
//				{
//					opt.setScript(Script.scriptOf(rs.getString("script")));
//					m.put(opt.getId(), opt);
//				}
//				catch (Exception e)
//				{
//					e.printStackTrace();
//				}
//			}
//		});
//
//		return m;
//	}

	private void fillDataSource(final Stack stack, Long envId)
	{
		jdbc.query("select * from t_env_ds where env_id = ?", new RowCallbackHandler()
		{
			@Override
			public void processRow(ResultSet rs) throws SQLException
			{
				String anchor = rs.getString("anchor");
				Long sourceId = rs.getLong("source_id");

				stack.set(anchor, sourceMgr.getSource(sourceId));
			}

		}, envId);

	}

	private void fillStack(final Stack stack, Long envId)
	{
		stack.set("nextId", nextId);
		stack.set("req", request);
		stack.set("post", post);
		stack.set("service", service);
		stack.set("jsonOf", jsonOf);
		stack.set("fold", fold);
		stack.set("unfold", unfold);
		stack.set("daystr", daystr);

		stack.set("MODEL", model);
		stack.set("IYB", iyb);

		jdbc.query("select * from t_env_define where env_id = ?", new RowCallbackHandler()
		{
			@Override
			public void processRow(ResultSet rs) throws SQLException
			{
				String consName = rs.getString("name");
				String valstr = rs.getString("value");
				int type = rs.getInt("type");

				Object val = null;
				if (type == 1)
					val = valstr;
				else if (type == 2)
					val = new BigDecimal(valstr);
				else if (type == 3)
					val = Integer.valueOf(valstr);
				else if (type == 4)
					val = JSON.parseObject(valstr);
				else if (type == 5)
					val = JSON.parseArray(valstr);
				else if (type == 6)
					val = Script.scriptOf(valstr).run(stack);

				setValue(stack, consName, val);
			}

		}, envId);

		jdbc.query("select * from t_env_function where env_id = ?", new RowCallbackHandler()
		{
			@Override
			public void processRow(ResultSet rs) throws SQLException
			{
				String consName = rs.getString("name");
				String params = rs.getString("params");
				String script = rs.getString("script");

				Log.debug("loading function... " + consName);

				Function f = new InnerFunction(Script.scriptOf(script), Common.isEmpty(params) ? null : params.split(","));
				setValue(stack, consName, f);
			}

		}, envId);
	}

	private void setValue(Stack stack, String consName, Object f)
	{
		String[] name = consName.split("[.]");
		if (name.length == 1)
		{
			stack.set(name[0], f);
		}
		else if (name.length == 2)
		{
			Map map = (Map)stack.get(name[0]);
			if (map == null)
			{
				map = new HashMap();
				stack.set(name[0], map);
			}
			map.put(name[1], f);
		}
	}

	private static class InnerFunction implements Function
	{
		Formula f;
		String[] params;

		public InnerFunction(Formula f, String[] params)
		{
			this.f = f;
			this.params = params;
		}

		@Override
		public Object run(Object[] objects, Factors factors)
		{
			Stack s = new Stack(factors);
			if (params != null && objects != null)
				for (int i = 0; i < params.length && i < objects.length; i++)
					s.set(params[i], objects[i]);

			return f.run(s);
		}
	}
}
