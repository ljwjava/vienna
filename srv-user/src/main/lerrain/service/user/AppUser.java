package lerrain.service.user;

import java.util.Map;

public class AppUser
{
    Long originalId;
    Long userId;

    Map info;

    public Long getOriginalId()
    {
        return originalId;
    }

    public void setOriginalId(Long originalId)
    {
        this.originalId = originalId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Map getInfo()
    {
        return info;
    }

    public void setInfo(Map info)
    {
        this.info = info;
    }
}