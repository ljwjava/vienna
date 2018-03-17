package lerrain.service.channel;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.Map;

@Controller
public class ChannelController
{
	@Autowired
	ChannelService channelSrv;

	@RequestMapping("/list.json")
	@ResponseBody
	public JSONObject list(@RequestBody JSONObject p)
	{
		Long platformId = p.getLong("platformId");

		int from = Common.intOf(p.get("from"), 0);
		int num = Common.intOf(p.get("number"), 10);

		JSONObject r = new JSONObject();
		r.put("list", channelSrv.list(null, from, num, platformId));
		r.put("total", channelSrv.count(null, platformId));

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", r);

		return res;
	}

	@RequestMapping("/contract/query.json")
	@ResponseBody
	public JSONObject queryContract(@RequestBody JSONObject p)
	{
		Long partyA = p.getLong("partyA");
		Long partyB = p.getLong("partyB");

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", channelSrv.queryContract(partyA, partyB));

		return res;
	}

	@RequestMapping("/contract/list.json")
	@ResponseBody
	public JSONObject listContract(@RequestBody JSONObject p)
	{
		Long companyId = p.getLong("companyId");

		int from = Common.intOf(p.get("from"), 0);
		int num = Common.intOf(p.get("number"), 10);

		JSONObject r = new JSONObject();
		r.put("list", channelSrv.listContract(companyId, from, num));
		r.put("total", channelSrv.countContract(companyId));

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", r);

		return res;
	}

	@RequestMapping("/contract/view.json")
	@ResponseBody
	public JSONObject viewContract(@RequestBody JSONObject p)
	{
		Long contractId = p.getLong("contractId");

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", channelSrv.viewContract(contractId));

		return res;
	}

	@RequestMapping("/contract/item.json")
	@ResponseBody
	public JSONObject addFee(@RequestBody JSONObject p)
	{
		Long contractId = p.getLong("contractId");
		Long clauseId = p.getLong("clauseId");

		Integer pay = p.getInteger("pay");
		Integer insure = p.getInteger("insure");
		Integer unit = p.getInteger("unit");

		channelSrv.addItem(contractId, clauseId, unit, pay, insure, null);

		JSONObject res = new JSONObject();
		res.put("result", "success");

		return res;
	}

	@RequestMapping("/contract/save.json")
	@ResponseBody
	public JSONObject saveContract(@RequestBody JSONObject p)
	{
		Long contractId = p.getLong("contractId");
		Long platformId = p.getLong("platformId");
		String name = p.getString("name");
		Long partyA = p.getLong("partyA");
		Long partyB = p.getLong("partyB");
		Date begin = p.getDate("begin");
		Date end = p.getDate("end");

		ChannelContract cc = new ChannelContract();
		cc.setId(contractId);
		cc.setPlatformId(platformId);
		cc.setName(name);
		cc.setPartyA(partyA);
		cc.setPartyB(partyB);
		cc.setBegin(begin);
		cc.setEnd(end);

		contractId = channelSrv.saveContract(cc);

		JSONArray detail = p.getJSONArray("detail");
		if (detail != null) for (int i = 0; i < detail.size(); i++)
		{
			JSONObject item = detail.getJSONObject(i);
			JSONArray rate = item.getJSONArray("rate");
			if (rate != null)
			{
				Double[] val = new Double[rate.size()];
				for (int j = 0; j < rate.size(); j++)
					val[j] = rate.getDouble(j);

				channelSrv.updateItem(item.getLong("itemId"), item.getInteger("unit"), val);
			}
		}

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", contractId);

		return res;
	}

	@RequestMapping("/contract/status.json")
	@ResponseBody
	public JSONObject status(@RequestBody JSONObject p)
	{
		Long contractId = p.getLong("contractId");
		int status = p.getIntValue("status");

		channelSrv.setContractStatus(contractId, status);

		JSONObject res = new JSONObject();
		res.put("result", "success");

		return res;
	}

	@RequestMapping("/contract/delete.json")
	@ResponseBody
	public JSONObject delete(@RequestBody JSONObject p)
	{
		Long contractId = p.getLong("contractId");
		channelSrv.deleteContract(contractId);

		JSONObject res = new JSONObject();
		res.put("result", "success");

		return res;
	}

//	@RequestMapping("/list_contract_rate.json")
//	@ResponseBody
//	public JSONObject listFee(@RequestBody JSONObject p)
//	{
//		Long contractId = p.getLong("contractId");
//
//		JSONObject res = new JSONObject();
//		res.put("result", "success");
//		res.put("content", channelSrv.getProductFee(contractId));
//
//		return res;
//	}

	@RequestMapping("/charge.json")
	@ResponseBody
	/**
	 * [{
	 *     productId: 产品ID
	 *     factors: {
	 *         pay:
	 *         insure:
	 *     }
	 *     price: 价格
	 *     bizNo: 业务号
	 * }, {
	 *     ...
	 * }]
	 * @param p
	 * @return
	 */
	public JSONObject charge(@RequestBody JSONObject p)
	{
		Log.info(p);

		Long platformId = p.getLong("platformId");
		Long vendorId = p.getLong("vendorId");
		Long agencyId = p.getLong("agencyId");
		String bizNo = p.getString("bizNo");

		Integer bizType = p.getInteger("bizType");
		Long bizId = p.getLong("bizId");

		Date time = new Date();

		JSONArray list = p.getJSONArray("detail");
		for (int i = 0; i < list.size(); i++)
		{
			JSONObject clause = list.getJSONObject(i);
			Long productId = clause.getLong("productId");
			double amount = clause.getDouble("amount");
			Map factors = clause.getJSONObject("factors");

			if (amount > 0)
				channelSrv.charge(platformId, vendorId, agencyId, productId, factors, bizType, bizId, bizNo, amount, time);
		}

		JSONObject res = new JSONObject();
		res.put("result", "success");

		return res;
	}

	@RequestMapping("/bill.json")
	@ResponseBody
	public JSONObject bill(@RequestBody JSONArray list)
	{
		Log.info(list);

		for (int i = 0; i < list.size(); i++)
		{
			JSONObject b = list.getJSONObject(i);
			Long platformId = b.getLong("platformId");
			Long vendorId = b.getLong("vendorId");
			Long payer = b.getLong("payer");
			Long drawer = b.getLong("drawer");
			String bizNo = b.getString("bizNo");
			Integer bizType = b.getInteger("bizType");
			Long bizId = b.getLong("bizId");
			Long productId = b.getLong("productId");
			Double amount = b.getDouble("amount");
			Date time = b.getDate("estimate");

			if (amount != null)
				channelSrv.bill(platformId, productId, payer, drawer, bizType, bizId, bizNo, vendorId, amount, time);
		}

		JSONObject res = new JSONObject();
		res.put("result", "success");

		return res;
	}

	@RequestMapping("/find_bill.json")
	@ResponseBody
	public JSONObject findBill(@RequestBody JSONObject c)
	{
		//条件组1
		Long platformId = c.getLong("platformId");
		Long vendorId = c.getLong("vendorId");
		String bizNo = c.getString("bizNo");

		//条件组2
		Integer bizType = c.getInteger("bizType");
		Long bizId = c.getLong("bizId");

		Object r = null;
		if (bizType != null && bizId != null)
			r = channelSrv.findBill(bizType, bizId);
		else if (platformId != null && vendorId != null && bizNo != null)
			r = channelSrv.findBill(platformId, vendorId, bizNo);

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", r);

		return res;
	}
}
