package lerrain.service.commission;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CommissionService
{
	@Autowired CommissionDao commissionDao;
	@Autowired IybPay iybPay;

	public void store(Commission commission)
	{
		Date now = new Date();

		if (commission.getEstimate() == null)
			commission.setEstimate(now);
		commission.setCreateTime(now);

		Long id = commissionDao.prepare(commission);

		if (commission.isAuto() && !now.before(commission.getEstimate()))
			payoff(commission);
	}

	/**
	 * 该方法执行未结束的时候
	 * 再次执行该方法会出现问题，需要加同步锁
	 * 而store的新佣金不会在loadCommissionReady的结果内出现，所以不会有影响
	 */
	public synchronized void payoffAll()
	{
		List<Commission> list = commissionDao.loadCommissionReady();

		if (list != null)
			for (Commission commission : list)
				payoff(commission);
	}

	public void payoff(Commission commission)
	{
		int status = 9;

		try
		{
			// Long userId, int type, int mode, double bonus, String activityCode, int day, String messageTitle, Long bizId, String bizNo, Long fromUserId, Long productId, double premium
			String productName = commission.getMemo();
			Long bizId = null;
			Long productId = null;
			double premium = 0;
			if((commission.getType() == 1 || commission.getType() == 2) && commission.getExtraInfoJson() != null){
				productName = commission.getExtraInfoJson().getString("productName");
				bizId = commission.getExtraInfoJson().getLong("policyId");
				productId = commission.getExtraInfoJson().getLong("productId");
				premium = commission.getExtraInfoJson().getDouble("premium");
			}

			JSONObject json = JSON.parseObject(iybPay.pay(commission.getUserId(), commission.getType(), commission.getUnit(), commission.getAmount(), productName, commission.getFreeze(), commission.getMemo(), bizId, commission.getBizNo(), commission.getFromUserId(), productId, premium));
			if (json != null && json.getBoolean("isSuccess"))
				status = 1;
		}
		catch (Exception e)
		{
			Log.alert(e);
		}

		commissionDao.pay(commission.getId(), status, new Date());
	}
}
