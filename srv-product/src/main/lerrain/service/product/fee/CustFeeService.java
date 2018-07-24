package lerrain.service.product.fee;

import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustFeeService
{
	@Autowired
	CustFeeDao feeDao;

	Map<Long, List<Map<String, Object>>> seekFactorsProduct;

	public void reset()
	{
		seekFactorsProduct = feeDao.loadFeeDefineFactors();

//		seekFactorsProduct = new HashMap<>();
//
//		for (Map<String, Object> cff : list)
//		{
//			List<CustFeeFactor> c = seekFactorsWare.get(cff.getWareId());
//			if (c == null)
//			{
//				c = new ArrayList<>();
//				seekFactorsWare.put(cff.getWareId(), c);
//			}
//
//			c.add(cff);
//
//			for (Long productId : cff.getProductIds())
//			{
//				List<CustFeeFactor> cc = seekFactorsProduct.get(productId);
//				if (cc == null)
//				{
//					cc = new ArrayList<>();
//					seekFactorsProduct.put(productId, cc);
//				}
//
//				cc.add(cff);
//			}
//		}
	}

	public CustFeeDefine getFeeDefine(Long schemeId, Long productId, Date time, Map seek)
	{
		return feeDao.findFeeRate(schemeId, productId, time, seek);
	}

	public List<CustFeeDefine> listFeeDefine(Long schemeId, Long productId)
	{
		return feeDao.listFeeRate(schemeId, productId);
	}

	public List<Map<String, Object>> listFeeDefineFactors(Long productId)
	{
		return seekFactorsProduct.get(productId);
	}

	public void saveFeeDefine(Long schemeId, Long productId, Date begin, Date end, List<CustFeeDefine> list)
	{
		feeDao.saveFeeRate(schemeId, productId, begin, end, list);
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

	public int countFeeByScheme(Long schemeId)
	{
		return feeDao.countFeeByScheme(schemeId);
	}

	public List<CustFeeDefine> queryFeeByScheme(Long schemeId, int from, int num)
	{
		return feeDao.queryFeeByScheme(schemeId, from, num);
	}

	public List<CustFeeDefine> queryFeeByScheme(Long schemeId)
	{
		return feeDao.queryFeeByScheme(schemeId);
	}

	public List<CustFeeDefine> queryTotalFeeRate(Long productId)
	{
		return feeDao.queryTotalFeeRate(productId);
	}

	public JSONObject deleteRate(JSONObject c){
		return feeDao.deleteRate(c);
	}
}
