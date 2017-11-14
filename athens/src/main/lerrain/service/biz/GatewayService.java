package lerrain.service.biz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GatewayService
{
    @Autowired
    GatewayDao gatewayDao;

    Map<Long, List<Gateway>> map;

    Map<String, Long> platformMap;

    public void reset()
    {
        map = gatewayDao.loadAllGateway();
    }

    public Long getPlatformId(String domain)
    {
//        return platformMap.get(domain);
        return 2L;
    }

    public Gateway getGateway(String domain, String uri)
    {
        Long platformId = getPlatformId(domain);

        List<Gateway> list = map.get(platformId);
        for (Gateway gateway : list)
            if (gateway.match(uri))
                return gateway;

        return null;
    }
}
