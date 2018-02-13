package lerrain.service.channel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ChannelService
{
    @Autowired
    ChannelDao channelDao;

    public List<Channel> list(String search, int from, int number, Long platformId)
    {
        return channelDao.list(search, from, number, platformId);
    }

    public int count(String search, Long platformId)
    {
        return channelDao.count(search, platformId);
    }

    public List<ChannelContract> getContractList(Long platformId, Long partyA, Long partyB)
    {
        return channelDao.loadContract(platformId, partyA, partyB);
    }

    public List<Map<String, Object>> getProductFee(Long contractId)
    {
        return channelDao.loadProductFee(contractId);
    }
}
