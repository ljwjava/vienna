package lerrain.service.channel;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class ChannelContract
{
    Long id;
    Long channelId;
    Long platformId;

    Long partyA, partyB;

    String name;

    Date begin, end;

    List<Long> docs;
    List<Map<String, Object>> feeDefine;

    Date updateTime;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getPartyA()
    {
        return partyA;
    }

    public void setPartyA(Long partyA)
    {
        this.partyA = partyA;
    }

    public Long getPartyB()
    {
        return partyB;
    }

    public void setPartyB(Long partyB)
    {
        this.partyB = partyB;
    }

    public Long getChannelId()
    {
        return channelId;
    }

    public void setChannelId(Long channelId)
    {
        this.channelId = channelId;
    }

    public Date getBegin()
    {
        return begin;
    }

    public void setBegin(Date begin)
    {
        this.begin = begin;
    }

    public Date getEnd()
    {
        return end;
    }

    public void setEnd(Date end)
    {
        this.end = end;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Long getPlatformId()
    {
        return platformId;
    }

    public void setPlatformId(Long platformId)
    {
        this.platformId = platformId;
    }

    public List<Map<String, Object>> getFeeDefine()
    {
        return feeDefine;
    }

    public void setFeeDefine(List<Map<String, Object>> feeDefine)
    {
        this.feeDefine = feeDefine;
    }

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }

    public List<Long> getDocs()
    {
        return docs;
    }

    public void setDocs(List<Long> docs)
    {
        this.docs = docs;
    }
}
