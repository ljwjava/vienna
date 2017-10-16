package lerrain.project.vienna;

import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;
import lerrain.tool.Network;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ForwardController
{
    @Value("${service.iyb}")
    private String srvUrl;

    @RequestMapping("/iybapi/**/*")
    @ResponseBody
    @CrossOrigin
    public JSONObject iyb(HttpServletRequest request)
    {
        try
        {
            String path = request.getRequestURI();
            String str = Common.stringOfACS(request.getInputStream(), "utf-8");
            String addr = srvUrl + path.substring(path.indexOf("/iybapi/") + 7);

            System.out.println("send: " + str + " to " + addr);
            String r = Network.request(addr, str, 3000);

            System.out.println("receive: " + r);

            JSONObject res = new JSONObject();
            res.put("result", "success");
            res.put("content", JSONObject.parseObject(r));

            return res;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
