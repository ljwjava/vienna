package lerrain.service.policy;

import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import lerrain.service.policy.upload.PolicyUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PolicyController
{
    @Autowired
    PolicyUploadService policyUploadSrv;

    @Autowired
    PolicyService policySrv;

    @Autowired
    TaskQueue queue;

    @RequestMapping("/upload/excel.json")
    @ResponseBody
    public JSONObject uploadExcel(@RequestBody JSONObject p)
    {
        String idempotent = p.getString("idempotent");
        Long userId = p.getLong("userId");

        queue.add(idempotent, policyUploadSrv.newTask(userId, p.getJSONObject("files").values().toArray()));

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }

    @RequestMapping("/view.json")
    @ResponseBody
    public JSONObject view(@RequestBody JSONObject p)
    {
        Long policyId = p.getLong("policyId");

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", policySrv.getPolicy(policyId));

        Log.info(res);

        return res;
    }
}
