package lerrain.service.fee;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class FeeService
{
	@Autowired
	FeeDao feeDao;

	PlatformFee platformFee;

//	Map<String, List<FeeDefine>> feeBase;
	FeeGrp feeGrp;

	public void reset()
	{
//		feeBase = feeDao.loadFeeDefine();
		feeGrp = feeDao.loadFeeDefine();

		platformFee = feeDao.loadPlatformScript();
	}

	public boolean charge(Long platformId, Date time, Object biz)
	{
		return platformFee.charge(platformId, time, biz);
	}

	public Object calc(Long platformId, Date time, Object biz)
	{
		return platformFee.calc(platformId, time, biz);
	}

	public boolean pay(Fee fee)
	{
		boolean r = false;

		try
		{
			r = platformFee.pay(fee, new Date());
		}
		catch (Exception e)
		{
			Log.error(e);
		}

		feeDao.pay(fee.getId(), r ? 1 : 9, new Date());

		return r;
	}

//	public List<FeeDefine> getFeeRate(Long platformId, Long agencyId, String group, String product, String payFreq, String payPeriod)
//	{
//		List<FeeDefine> list = feeBase.get(platformId + "/" + agencyId + "/" + group + "/" + product + "/" + payFreq + "/" + payPeriod);
//
//		if (list == null)
//			return null;
//
//		Date now = new Date();
//		List<FeeDefine> r = new ArrayList<>();
//
//		for (FeeDefine pc : list)
//		{
//			if (pc.match(now))
//				r.add(pc);
//		}
//
//		return r;
//	}

	public List<FeeDefine> getFeeRate(Long platformId, Long agencyId, String group, String product, Object[] vals)
	{
		return feeGrp.find(platformId + "/" + agencyId + "/" + group + "/" + product, vals, new Date());
	}

	public void store(Fee commission)
	{
		Date now = new Date();

		if (commission.getEstimate() == null)
			commission.setEstimate(now);
		commission.setCreateTime(now);

		Long id = feeDao.prepare(commission);

		if (commission.isAuto() && !now.before(commission.getEstimate()))
			pay(commission);
	}

	public void retry(List<Long> ids)
	{
		if (ids != null && ids.size() > 0)
		{
			for (Long id : ids)
			{
				Fee c = feeDao.loadFee(id);
				if (c != null && c.getAmount() > 0)
				{
					pay(c);
				}
			}
		}
	}

	/**
	 * 该方法执行未结束的时候
	 * 再次执行该方法会出现问题，需要加同步锁
	 * 而store的新佣金不会在loadCommissionReady的结果内出现，所以不会有影响
	 */
	public synchronized void payAll()
	{
		List<Fee> list = feeDao.loadFeeReady();

		if (list != null)
			for (Fee fee : list)
				pay(fee);
	}
}
