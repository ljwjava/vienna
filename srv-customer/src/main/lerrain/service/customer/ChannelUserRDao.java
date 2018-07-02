package lerrain.service.customer;

import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class ChannelUserRDao
{
	@Autowired
	JdbcTemplate jdbc;
	
	public boolean isExisted(Integer type, String userId)
	{
		String sql = "select count(1) from t_channel_user_r where channel_type = ? and channel_user_id = ?";
		return jdbc.queryForObject(sql, new Object[] {type, userId}, Integer.class) > 0;
	}

	public ChannelUserR load(Integer type, String userId)
	{
		String sql;
		
		sql = "select cu.* from t_channel_user_r cu where cu.channel_type = ? and cu.channel_user_id = ?";
		Map<String, Object> m = null;
		try{
            m = jdbc.queryForMap(sql, new Object[] {type, userId});
        }catch(EmptyResultDataAccessException emptye){
		    return null;
        }
		if(Common.isEmpty(m)){
			return null;
		}

		ChannelUserR cu = new ChannelUserR();
		cu.setChannelType(Common.toInteger(m.get("channel_type")));
		cu.setChannelUserId((String) m.get("channel_user_id"));
		cu.setAccountId(Common.toLong(m.get("rela_account_id")));
		cu.setCreateTime(Common.dateOf(m.get("create_time"), null));
		cu.setUpdateTime(Common.dateOf(m.get("update_time"), null));

		return cu;
	}

	// 修改accountId
	public void updateAccountId(ChannelUserR cu)
	{
		String sql = "update t_channel_user_r set rela_account_id = ?, update_time = now() where channel_type = ? and channel_user_id = ?";
		jdbc.update(sql, cu.getAccountId(), cu.getChannelType(), cu.getChannelUserId());
	}

	// 保存数据
	public int insert(ChannelUserR cu) {
		return jdbc.update("insert into t_channel_user_r (channel_type, channel_user_id, rela_account_id, create_time, update_time) values (?,?,?,now(),now())", cu.getChannelType(), cu.getChannelUserId(), cu.getAccountId());
	}

	// 保存数据流水
	public int insertSerial(ChannelUserR cu) {
		return jdbc.update("insert into t_channel_user_r_serial (channel_type, channel_user_id, rela_account_id, create_time, update_time) values(?, ?, ?, now(), now())", cu.getChannelType(), cu.getChannelUserId(), cu.getAccountId());
//		return jdbc.update("insert into t_channel_user_r_serial (channel_type, channel_user_id, rela_account_id, create_time, update_time) " +
//				"select cu.channel_type, cu.channel_user_id, cu.rela_account_id, cu.update_time, now() from t_channel_user_r cu where cu.channel_type = ? and cu.channel_user_id = ?", cu.getChannelType(), cu.getChannelUserId());
	}
}
