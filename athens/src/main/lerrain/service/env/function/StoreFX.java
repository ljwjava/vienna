package lerrain.service.env.function;

import com.alibaba.fastjson.JSON;
import lerrain.service.biz.GatewayService;
import lerrain.service.common.Log;
import lerrain.service.env.ScriptErrorException;
import lerrain.service.task.TaskQueue;
import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import lerrain.tool.script.ScriptRuntimeException;
import lerrain.tool.script.Stack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class StoreFX extends HashMap<String, Object>
{
    @Autowired
    JdbcTemplate jdbc;

    String loadSql = "select content from t_store where name = ? and `key` = ?";
    String saveSql = "replace into t_store(name, `key`, content, update_time) values(?, ?, ?, now())";

    public StoreFX()
    {
        this.put("save", new Function()
        {
            @Override
            public Object run(final Object[] objects, final Factors factors)
            {
                String name = objects[0].toString();
                String str = objects[1] == null ? null : objects[1].toString();

                String key = objects.length > 2 ? Common.trimStringOf(objects[2]) : "";
                if (key == null)
                    key = "";

                jdbc.update(saveSql, name, key, str);
                return null;
            }
        });

        this.put("load", new Function()
        {
            @Override
            public Object run(final Object[] objects, final Factors factors)
            {
                String name = objects[0].toString();

                String key = objects.length > 1 ? Common.trimStringOf(objects[1]) : "";
                if (key == null)
                    key = "";

                return jdbc.queryForObject(loadSql, String.class, name, key);
            }
        });

    }


}
