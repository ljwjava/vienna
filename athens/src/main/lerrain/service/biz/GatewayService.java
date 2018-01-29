package lerrain.service.biz;

import lerrain.service.common.Log;
import lerrain.service.env.EnvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GatewayService
{
    @Autowired
    GatewayDao gatewayDao;

    Map<String, List<Gateway>> map;

    public void reset(EnvService envSrv)
    {
        map = gatewayDao.loadAllGateway(envSrv);
    }

    public Gateway getGateway(Long gatewayId)
    {
        for (String sort : map.keySet())
            for (Gateway gateway : map.get(sort))
                if (gateway.getId() == gatewayId)
                    return gateway;

        return null;
    }

    public List<Gateway> getGatewayList(String sort)
    {
        return map.get(sort);
    }

    public Gateway getGateway(String uri)
    {
        String sort = uri.substring(0, uri.indexOf("/"));
        List<Gateway> list = map.get(sort);

        if (list == null)
            return null;

        for (Gateway gateway : list)
            if (gateway.match(uri))
                return gateway;

        return null;
    }
}
