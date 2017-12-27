package lerrain.service.commission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CmsDefineService
{
    @Autowired CmsDefineDao cmsDefineDao;

    Map<String, List<CmsDefine>> commissionBase;

    public void reset()
    {
        commissionBase = cmsDefineDao.loadCommissionDefine();
    }

    public List<CmsDefine> getCommissionRate(Long platformId, String group, String product, String payFreq, String payPeriod)
    {
        List<CmsDefine> list = commissionBase.get(platformId + "/" + group + "/" + product + "/" + payFreq + "/" + payPeriod);

        if (list == null)
            return null;

        List<CmsDefine> r = new ArrayList<>();

        for (CmsDefine pc : list)
        {
            if (pc.match())
                r.add(pc);
        }

        return r;
    }
}
