package lerrain.service.biz;

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

    public Map<Long, List<Gateway>> loadAllGateway()
    {
        final Map<Long, List<Gateway>> m = new HashMap<>();

        String sql = "select * from t_gateway where valid is null order by seq";

        jdbc.query(sql, new RowCallbackHandler()
        {
            @Override
            public void processRow(ResultSet rs) throws SQLException
            {
                int type = rs.getInt("type");
                Long platformId = rs.getLong("platform_id");
                String uri = rs.getString("uri");
                String with = rs.getString("with");
                String script = rs.getString("script");
                String forwardTo = rs.getString("forward_to");

                boolean needLogin = "Y".equalsIgnoreCase(rs.getString("login"));
                boolean supportGet = !"N".equalsIgnoreCase(rs.getString("support_get"));
                boolean supportPost = !"N".equalsIgnoreCase(rs.getString("support_post"));
                boolean forward = !"N".equalsIgnoreCase(rs.getString("forward"));

                int support = (supportGet ? Gateway.SUPPORT_GET : 0) | (supportPost ? Gateway.SUPPORT_POST : 0);

                List<Gateway> list = m.get(platformId);
                if (list == null)
                {
                    list = new ArrayList<>();
                    m.put(platformId, list);
                }

                Gateway gw = new Gateway();
                gw.setType(type);
                gw.setLogin(needLogin);
                gw.setSupport(support);
                gw.setWith(with == null ? null : with.split(","));
                gw.setUri(uri);
                gw.setScript(Script.scriptOf(script));
                gw.setForward(forward);
                gw.setForwardTo(forwardTo);
                gw.setPlatformId(platformId);

                list.add(gw);
            }

        });

        return m;
    }
}
