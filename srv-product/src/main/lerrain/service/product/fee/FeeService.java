package lerrain.service.product.fee;

import lerrain.service.common.Log;
import lerrain.tool.Common;
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

	@Autowired
	IybPay iybPay;

	@Autowired
	IybPushMsg iyPush;

	public List<FeeDefine> listFeeDefine(Long platformId, Long productId)
	{
		return feeDao.listFeeRate(platformId, productId);
	}

	public List<FeeDefine> getFeeDefine(Long platformId, Long productId, Map factors, Date time)
	{
		List<FeeDefine> r = new ArrayList<>();

		List<FeeDefine> list = feeDao.listFeeRate(platformId, productId, factors);
		for (FeeDefine fd : list)
		{
			if (fd.match(time))
				r.add(fd);
		}

		return r;
	}

	public void bill(List<Fee> list)
	{
		for (Fee r : list)
			if (Math.abs(r.amount) > 0.005f)
				feeDao.prepare(r);
	}

	public List<Fee> findFee(Long platformId, Long vendorId, String bizNo)
	{
		return feeDao.findFee(platformId, vendorId, bizNo);
	}

	public List<Fee> findFee(Integer bizType, Long bizId)
	{
		return feeDao.findFee(bizType, bizId);
	}

	public int payAll(Long platformId, Long vendorId, String bizNo)
	{
		int r = 0;

		List<Fee> list = feeDao.loadFeeReady(platformId, vendorId, bizNo);
		for (Fee fee : list)
		{
			if (!pay(fee))
				r++;
		}

		return r;
	}

	public void pay(List<Long> list)
	{
		for (Long id : list)
		{
			Fee fee = feeDao.loadFee(id);
			pay(fee);
		}
	}

	public boolean pay(Fee fee)
	{
		boolean r = false;

		try
		{
			Long iybPolicyId = null;
			Long productId = null;
			Long fromUserId = null;

			double premium = 0;

			String productName = null;
			String fromUserName = null;

			Map map = fee.getExtra();
			if (map != null)
			{
				iybPolicyId = Common.toLong(map.get("iybPolicyId"));
				fromUserId = Common.toLong(map.get("fromUserId"));
				productId = Common.toLong(map.get("productId"));
				premium = Common.toDouble(map.get("premium"));
				productName = Common.trimStringOf(map.get("productName"));
				fromUserName = Common.trimStringOf(map.get("fromUserName"));
			}

			if (premium > 0 && fee.getAmount() > premium)
				throw new RuntimeException("发放佣金["+fee.getAmount()+"]应小于保费["+premium+"]");

			r = iybPay.pay(fee.getDrawer(), fee.getType(), fee.getAmount(), productName, fee.getFreeze(), fee.getMemo(), iybPolicyId, fee.getBizNo(), fromUserId, productId, premium);
			if (r)
			{
				if (fee.getType() == 1)
					iyPush.send(fee.getDrawer(), "tips", "self","提醒", "一笔热腾腾的推广费到账啦！点击这里，加油赚云宝！", String.format("您有%.2f元推广费已到云宝账户，还有更多高推广费产品，再接再厉！封神之日可待！独乐乐不如众乐乐，邀请好友分享云宝赚钱秘籍！", fee.getAmount()), null);
				else if (fee.getType() == 2)
					iyPush.send(fee.getDrawer(), "tips", "self","提醒",  "您的伙伴推广成功，您的云宝新增了一笔收入！点此查看谁帮我在赚钱", String.format("恭喜您！您的伙伴%s帮您赚取了%.2f元！更多热销产品在线，快去分享给小伙伴一起交流云宝心得！", fromUserName, fee.getAmount()), null);
				else if (fee.getType() == 4)
					iyPush.send(fee.getDrawer(), "tips", "self","提醒",  "一笔热腾腾的奖金到账啦！点击这里，加油赚云宝！", String.format("您有%.2f元额外推广奖金已到云宝账户，还有更多高推广费产品，再接再厉！封神之日可待！独乐乐不如众乐乐，邀请好友分享云宝赚钱秘籍！", fee.getAmount()), null);
			}
		}
		catch (Exception e)
		{
			Log.error(e);
		}

		feeDao.pay(fee.getId(), r ? 1 : 9, new Date());

		return r;
	}

}
