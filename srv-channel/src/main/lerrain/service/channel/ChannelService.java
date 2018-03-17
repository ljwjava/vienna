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

    public List<ChannelContract> queryContract(Long partyA, Long partyB)
    {
        return channelDao.queryContract(partyA, partyB);
    }

    public ChannelContract viewContract(Long contractId)
    {
        return channelDao.loadContract(contractId);
    }

    public Long saveContract(ChannelContract contract)
    {
        return channelDao.saveContract(contract);
    }

    public void setContractStatus(Long contractId, int status)
    {
        channelDao.updateContract(contractId, status);
    }

    public void deleteContract(Long contractId)
    {
        channelDao.deleteContract(contractId);
    }

    public List<ChannelContract> listContract(Long companyId, int from, int to)
    {
        return channelDao.listContract(companyId, from, to);
    }

    public int countContract(Long companyId)
    {
        return channelDao.countContract(companyId);
    }

    public void charge(Long platformId, Long vendorId, Long agencyId, Long productId, Map factors, int bizType, Long bizId, String bizNo, double premium, Date time)
    {
        Map<String, Double> map = new HashMap<>();
        Calendar calendar = Calendar.getInstance();

        for (FeeDefine c : getFeeDefine(platformId, agencyId, productId, factors, time))
        {
            calendar.setTime(time);
            int year = calendar.get(Calendar.YEAR);

            for (int i = 0; i < c.getFeeRate().length; i++)
            {
                BigDecimal fr = c.getFeeRate()[i];
                if (fr != null)
                {
                    double amt = 0;
                    if (c.getUnit() == 1)
                        amt = premium * fr.doubleValue();
                    else if (c.getUnit() == 2)
                        amt = fr.doubleValue();
                    else if (c.getUnit() == 3)
                        amt = premium * fr.doubleValue() / 100;
                    else if (c.getUnit() == 4)
                    {
                        Double val = map.get(c.getPayer() + "/" + i);
                        if (val != null)
                            amt = val * fr.doubleValue() / 100;
                    }

                    if (amt > 0)
                    {
                        calendar.set(Calendar.YEAR, year + i);
                        bill(c.getPlatformId(), c.getProductId(), c.getPayer(), c.getDrawer(), bizType, bizId, bizNo, vendorId, BigDecimal.valueOf(amt).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue(), calendar.getTime());

                        Double val = map.get(c.getDrawer() + "/" + i);
                        map.put(c.getDrawer() + "/" + i, val == null ? amt : val + amt);
                    }
                }
            }
        }
    }

    public void bill(Long platformId, Long productId, Long payer, Long drawer, int bizType, Long bizId, String bizNo, Long vendorId, double amt, Date time)
    {
        if (Math.abs(amt) > 0.005f)
            channelDao.bill(platformId, productId, payer, drawer, bizType, bizId, bizNo, vendorId, amt, 1, time);
    }

    public List findBill(Long platformId, Long vendorId, String bizNo)
    {
        return channelDao.findBill(platformId, vendorId, bizNo);
    }

    public List findBill(int bizType, Long bizId)
    {
        return channelDao.findBill(bizType, bizId);
    }

    public List<FeeDefine> getFeeDefine(Long platformId, Long agencyId, Long productId, Map factors, Date time)
    {
        List<FeeDefine> r = new ArrayList<>();

        for (FeeDefine fd : channelDao.loadChannelFeeDefine(platformId, agencyId, productId, factors))
        {
            if (fd.getFeeRate() != null && fd.match(time))
                r.add(fd);
        }

        return r;
    }

    public void addItem(Long contractId, Long clauseId, Integer unit, Integer pay, Integer insure, Double[] val)
    {
        if (val == null)
            val = new Double[5];

        if (val.length < 5)
            val = Arrays.copyOf(val, 5);

        channelDao.addItem(contractId, clauseId, unit, pay, insure, val);
    }

    public void updateItem(Long itemId, Integer unit, Double[] val)
    {
        if (itemId == null)
            return;

        if (val == null)
            val = new Double[5];

        if (val.length < 5)
            val = Arrays.copyOf(val, 5);

        channelDao.updateItem(itemId, unit, val);
    }
}
