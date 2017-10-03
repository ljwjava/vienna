package lerrain.project.vienna;

import com.alibaba.fastjson.JSONObject;
import lerrain.project.vienna.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@ControllerAdvice
public class SystemController
{
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public JSONObject exc(RuntimeException e, HttpServletResponse response)
    {
        JSONObject res = new JSONObject();
        res.put("result", "fail");
        res.put("reason", e.getMessage());

        return res;
    }
}
