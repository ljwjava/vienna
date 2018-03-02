package lerrain.service.product;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Map;

@Controller
public class ProductController
{
    @RequestMapping("/reset")
    @ResponseBody
    public String reset()
    {
        return "success";
    }
}
