package lerrain.service.biz;

import lerrain.service.common.Log;
import lerrain.service.env.EnvService;
import lerrain.tool.Common;
import lerrain.tool.script.Script;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by lerrain on 2017/11/13.
 */
@Repository
public class GatewayDao
{
    @Autowired
    JdbcTemplate jdbc;

    public Map<String, List<Gateway>> loadAllGateway(final EnvService envSrv)
    {
        final Map<String, List<Gateway>> m = new HashMap<>();

        jdbc.query("select * from t_gateway where valid is null order by seq desc", new RowCallbackHandler()
        {
            @Override
            public void processRow(ResultSet rs) throws SQLException
            {

                int type = rs.getInt("type");
                Long id = rs.getLong("id");
                Long envId = rs.getLong("env_id");
                String uri = rs.getString("uri");
                String with = rs.getString("with");
                String script = rs.getString("script");
                int forward = rs.getInt("forward");
                String forwardTo = rs.getString("forward_to");

                if (!envSrv.isValid(envId))
                    return;

                boolean needLogin = "Y".equalsIgnoreCase(rs.getString("login"));
                boolean supportGet = !"N".equalsIgnoreCase(rs.getString("support_get"));
                boolean supportPost = !"N".equalsIgnoreCase(rs.getString("support_post"));

                int support = (supportGet ? Gateway.SUPPORT_GET : 0) | (supportPost ? Gateway.SUPPORT_POST : 0);

                String sort = uri.substring(0, uri.indexOf("/"));
                List<Gateway> list = m.get(sort);
                if (list == null)
                {
                    list = new ArrayList<>();
                    m.put(sort, list);
                }

                Gateway gw = new Gateway();
                gw.setId(id);
                gw.setType(type);
                gw.setLogin(needLogin);
                gw.setSupport(support);
                gw.setUri(uri);
                gw.setForward(forward);
                gw.setForwardTo(forwardTo);
                gw.setEnv(envSrv.getEnv(envId));

                try
                {
                    if (!Common.isEmpty(with))
                    {
                        Map map = new HashMap();
                        String[] ss = with.split(",");
                        for (String str : ss)
                        {
                            if (str.indexOf(":") > 0)
                            {
                                String[] s = str.split("[:]");
                                map.put(s[0], "#" + s[1]);
                            }
                            else
                            {
                                String[] s = str.split("[=]");
                                map.put(s[0], s[1]);
                            }
                        }
                        gw.setWith(map);
                    }
                    gw.setScript(Script.scriptOf(script));
                }
                catch (Exception e)
                {
                    Log.error("<" + gw.getId() + "> " + gw.getUri() + " error");
                }

                try
                {
                    gw.setScript(Script.scriptOf(script));
                }
                catch (Exception e)
                {
                    Log.error("<" + gw.getId() + "> " + gw.getUri() + " error");
                }

                list.add(gw);
            }

        });

        return m;
    }
}
