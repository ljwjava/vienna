package lerrain.service.channel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
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

    public void bill(Long platformId, Long vendorId, Long agencyId, String productId, Map factors, String bizNo, double premium, Date time)
    {
//        Long vendorId = channelDao.getVendor(productId);

        for (FeeDefine fd : getFeeDefine(platformId, agencyId, productId, factors, time))
            channelDao.bill(bizNo, vendorId, premium, fd, time);
    }

    public List listBill(Long platformId, Long vendorId, String bizNo)
    {
        return channelDao.listBill(platformId, vendorId, bizNo);
    }

    public List<FeeDefine> getFeeDefine(Long platformId, Long agencyId, String productId, Map factors, Date time)
    {
        List<FeeDefine> r = new ArrayList<>();

        for (FeeDefine fd : channelDao.loadChannelFeeDefine(platformId, agencyId, productId, factors))
        {
            if (fd.match(time))
                r.add(fd);
        }

        return r;
    }
}
