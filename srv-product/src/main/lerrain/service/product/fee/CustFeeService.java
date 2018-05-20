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
public class CustFeeService
{
	@Autowired
	CustFeeDao feeDao;

	public List<CustFeeDefine> listFeeDefine(Long schemeId, Long productId)
	{
		return feeDao.listFeeRate(schemeId, productId);
	}

	public List<CustFeeDefine> listFeeDefineKeys(Long productId)
	{
		return null;
	}

	public void saveFeeDefine(Long schemeId, Long productId, List<CustFeeDefine> list)
	{
		feeDao.saveFeeRate(schemeId, productId, list);
	}

	public List<CustFeeDefine> getFeeDefine(Long schemeId, Long productId, Map factors, Date time)
	{
		List<CustFeeDefine> r = new ArrayList<>();

		List<CustFeeDefine> list = feeDao.listFeeRate(schemeId, productId);
		for (CustFeeDefine fd : list)
		{
			if (fd.match(time, factors))
				r.add(fd);
		}

		return r;
	}
}
