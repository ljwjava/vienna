package lerrain.service.biz;

import lerrain.service.common.Log;
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

    public void reset()
    {
        map = gatewayDao.loadAllGateway();
    }

    public List<Gateway> getGatewayList(String sort)
    {
        return map.get(sort);
    }

    public Gateway getGateway(String uri)
    {
        Log.debug(uri);

        String sort = uri.substring(0, uri.indexOf("/"));
        List<Gateway> list = map.get(sort);

        for (Gateway gateway : list)
            if (gateway.match(uri))
                return gateway;

        return null;
    }
}
