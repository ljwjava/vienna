package lerrain.service.channel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

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
        Map<String, Double> map = new HashMap<>();

        for (FeeDefine c : getFeeDefine(platformId, agencyId, productId, factors, time))
        {
            for (int i = 0; i < c.getFeeRate().length; i++)
            {
                BigDecimal fr = c.getFeeRate()[i];
                if (fr != null)
                {
                    double amt = 0;
                    if (c.getUnit() == 1)
                        amt = Math.round(premium * fr.doubleValue() * 100) / 100.0f;
                    else if (c.getUnit() == 2)
                        amt = fr.doubleValue();
                    else if (c.getUnit() == 3)
                        amt = Math.round(premium * fr.doubleValue()) / 100.0f;
                    else if (c.getUnit() == 4)
                    {
                        Double val = map.get(c.getPayer() + "/" + i);
                        if (val != null)
                            amt = Math.round(val * fr.doubleValue()) / 100.0f;
                    }

                    if (amt > 0)
                    {
                        channelDao.bill(c, bizNo, vendorId, amt, 1, time);

                        Double val = map.get(c.getDrawer() + "/" + i);
                        map.put(c.getDrawer() + "/" + i, val == null ? amt : val + amt);
                    }
                }
            }
        }
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
            if (fd.getFeeRate() != null && fd.match(time))
                r.add(fd);
        }

        return r;
    }
}
