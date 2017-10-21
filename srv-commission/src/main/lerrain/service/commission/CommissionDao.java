package lerrain.service.commission;

import lerrain.service.common.ServiceTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class CommissionDao
{
	@Autowired JdbcTemplate jdbc;
	@Autowired ServiceTools tools;

	public Long prepare(Commission c) //(Long platformId, Long userId, double amount, int type, int mode, Date estimate, int freeze, Long productId, String bizNo, boolean auto, String memo, Date now)
	{
		c.setId(tools.nextId("commission"));

		jdbc.update("insert into t_commission(id, biz_no, product_id, amount, type, unit, estimate, freeze, pay, status, platform_id, user_id, from_user_id, auto, memo, extra_info, create_time) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
				c.getId(), c.getBizNo(), c.getProductId(), c.getAmount(), c.getType(), c.getUnit(), c.getEstimate(), c.getFreeze(), null, 0, c.getPlatformId(), c.getUserId(), c.getFromUserId(), c.isAuto() ? "Y" : "N", c.getMemo(), c.getExtraInfo(), c.getCreateTime());

		return c.getId();
	}

	public List<Commission> loadCommissionReady()
	{
		List<Commission> r = null;

		List<Map<String, Object>> list = jdbc.queryForList("select * from t_commission where auto = 'Y' and status = 0 and estimate <= now()");
		if (list != null)
		{
			r = new ArrayList<>();
			for (Map<String, Object> map : list)
				r.add(Commission.commissionOf(map));
		}

		return r;
	}

	public Commission loadCommissionById(Long id)
	{
		Commission c = null;
		Map<String, Object> map = jdbc.queryForMap("select * from t_commission where status in (0,9) and estimate <= now() and id = ?", id);
		if(map != null)
		{
			c = Commission.commissionOf(map);
		}
		return c;
	}

	public void pay(Long id, int status, Date time)
	{
		jdbc.update("update t_commission set pay = ?, status = ? where id = ?", time, status, id);
	}
}
