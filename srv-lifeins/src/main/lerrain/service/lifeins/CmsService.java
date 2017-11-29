package lerrain.service.lifeins;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CmsService
{
    @Autowired CmsDao cmsmDao;

    Map<String, List<CmsDefine>> commissionBase;

    public void reset()
    {
        commissionBase = cmsmDao.loadCommissionDefine();
    }

    public List<CmsDefine> getCommissionRate(Long platformId, String group, Long packId, String payFreq, String payPeriod)
    {
        List<CmsDefine> list = commissionBase.get(platformId + "/" + group + "/" + packId + "/" + payFreq + "/" + payPeriod);

        if (list == null)
            return null;

        List<CmsDefine> r = new ArrayList<>();

        for (CmsDefine pc : list)
        {
            if (pc.match())
                r.add(pc);
        }

//        double[][] r = new double[2][];
//
//        for (CmsDefine pc : list)
//        {
//            if (pc.match())
//            {
//                r[0] = pc.getSelfRate(r[0]);
//                r[1] = pc.getParentRate(r[1]);
//            }
//        }

        return r;
    }
}
