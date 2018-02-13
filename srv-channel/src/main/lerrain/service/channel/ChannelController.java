package lerrain.service.channel;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

	@RequestMapping("/list_contract.json")
	@ResponseBody
	public JSONObject listContract(@RequestBody JSONObject p)
	{
		Long platformId = p.getLong("platformId");
		Long partyA = p.getLong("partyA");
		Long partyB = p.getLong("partyB");

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", channelSrv.getContractList(platformId, partyA, partyB));

		return res;
	}

	@RequestMapping("/list_productfee.json")
	@ResponseBody
	public JSONObject listFee(@RequestBody JSONObject p)
	{
		Long contractId = p.getLong("contractId");

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", channelSrv.getProductFee(contractId));

		return res;
	}

	@RequestMapping("/bill.json")
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
	public JSONObject bill(@RequestBody JSONArray list)
	{
		JSONObject res = new JSONObject();
		res.put("result", "success");

		return res;
	}
}
