package lerrain.service.customer;

import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChannelUserRService
{
	@Autowired
	ChannelUserRDao cuDao;

	/**
	 * 查询并绑定数据（顺便记录流水）
	 * @param cur
	 * @return
	 */
	public ChannelUserR searchAndBind(ChannelUserR cur)
	{
		if(Common.isEmpty(cur) || Common.isEmpty(cur.getChannelType()) || Common.isEmpty(cur.getChannelUserId())){
			throw new RuntimeException("渠道类型和渠道用户ID不能为空");
		}
		ChannelUserR curDb = null;
		try{
			curDb = cuDao.load(cur.getChannelType(), cur.getChannelUserId());
			if(curDb != null){
				// 传了新的accountId，且与当前库内accountId不一致（是否换了accountId）
				if(!Common.isEmpty(cur.getAccountId()) && cur.getAccountId() != curDb.getAccountId()) {
					cuDao.updateAccountId(cur);
					cuDao.insertSerial(cur);
                    curDb = cur;
				}
			}else{
			    if(!Common.isEmpty(cur.getAccountId())) {
                    cuDao.insert(cur);
                    cuDao.insertSerial(cur);
                    curDb = cur;
                }
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException("绑定渠道信息异常，请稍后重试");
		}

		return curDb;
	}

}
