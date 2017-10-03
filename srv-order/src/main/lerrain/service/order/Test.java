package lerrain.service.order;

import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Network;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
public class Test
{
//    @Resource
//    OrderDaoMongo orderDao;
//
//    @RequestMapping({"/test.do"})
//    @ResponseBody
//    @CrossOrigin
//    public JSONObject save()
//    {
//        List<Order> list = orderDao.find("idb");
//        for (Order order : list)
//        {
//            if (order.getPay() == 2)
//            {
//                Map json = order.getDetail();
//                if (json != null)
//                {
//                    Map v1 = (Map)json.get("applicant");
//                    if (v1 != null)
//                    {
//                        v1.put("platformId", 1);
//                        v1.put("owner", 1);
//                        Network.request("http://localhost:7602/save.json", JSONObject.toJSONString(v1));
//                    }
//
//                    Map v2 = (Map)json.get("insurant");
//                    if (v2 != null)
//                    {
//                        v2.put("platformId", 1);
//                        v2.put("owner", 1);
//                        Network.request("http://localhost:7602/save.json", JSONObject.toJSONString(v2));
//                    }
//                }
//            }
//        }
//
//        JSONObject res = new JSONObject();
//        res.put("result", "success");
//
//        return res;
//    }
}
