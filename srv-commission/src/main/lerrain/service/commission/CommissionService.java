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
	@Autowired PushMsg pushMsg;

	private static final String msgTitle = "一笔热腾腾的推广费到账啦！点击这里，加油赚云宝！";
	private static final String msgContent = "您有%.2f元推广费已到云宝账户，还有更多高推广费产品，再接再厉！封神之日可待！独乐乐不如众乐乐，邀请好友分享云宝赚钱秘籍！";
	private static final String msgParentTitle = "您的伙伴推广成功，您的云宝新增了一笔收入！点此查看谁帮我在赚钱";
	private static final String msgParentContent = "恭喜您！您的伙伴%s帮您赚取了%.2f元！更多热销产品在线，快去分享给小伙伴一起交流云宝心得！";

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
			String fromUserName = "";
			if((commission.getType() == 1 || commission.getType() == 2) && commission.getExtraInfoJson() != null){
				productName = commission.getExtraInfoJson().getString("productName");
				bizId = commission.getExtraInfoJson().getLong("policyId");
				productId = commission.getExtraInfoJson().getLong("productId");
				premium = commission.getExtraInfoJson().getDouble("premium");
				fromUserName = commission.getExtraInfoJson().getString("fromUserName");
				fromUserName = "null".equals(fromUserName) ? "" : fromUserName;
			}

			JSONObject json = JSON.parseObject(iybPay.pay(commission.getUserId(), commission.getType(), commission.getUnit(), commission.getAmount(), productName, commission.getFreeze(), commission.getMemo(), bizId, commission.getBizNo(), commission.getFromUserId(), productId, premium));
			if (json != null && json.getBoolean("isSuccess")){
				status = 1;
				// 佣金推送消息
				if(commission.getType() == 1){
					String r = pushMsg.send(String.valueOf(commission.getUserId()), "tips", "self", "提醒", msgTitle, String.format(msgContent, commission.getAmount()), null);
					if(r == null){
						System.out.println("佣金消息推送异常：" + commission.getId());
					}
				}else if(commission.getType() == 2){
					String r = pushMsg.send(String.valueOf(commission.getUserId()), "tips", "self", "提醒", msgParentTitle, String.format(msgParentContent, fromUserName, commission.getAmount()), null);
					if(r == null){
						System.out.println("佣金消息推送异常：" + commission.getId());
					}
				}
			}
		}
		catch (Exception e)
		{
			Log.alert(e);
		}

		commissionDao.pay(commission.getId(), status, new Date());
	}
}
