package lerrain.service.dict;

import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class IdController
{
    @Autowired
    JdbcTemplate jdbc;

    String sql = "select s_next_seq(?) from dual";

    int skip = -1;

    @RequestMapping("/id/req")
    @ResponseBody
    public synchronized String reqId(@RequestBody String code)
    {
        if(skip <= 0){
            skip = jdbc.queryForObject("select step from s_sequence where code = ?", Integer.class, code);
        }

        long r1 = jdbc.queryForObject(sql, Long.class, code);
        long r0 = r1 - skip + 1;

        return r0 + "," + r1;
    }

}
