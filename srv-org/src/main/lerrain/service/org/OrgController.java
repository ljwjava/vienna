package lerrain.service.org;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class OrgController
{
    @Autowired
    OrgService orgSrv;

    @RequestMapping("/member.json")
    @ResponseBody
    public JSONObject member(@RequestBody JSONObject json)
    {
        Long memberId = json.getLong("memberId");
        Long userId = json.getLong("userId");

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", memberId != null ? orgSrv.getMember(memberId) : orgSrv.findMember(userId));

        return res;
    }
}
