package lerrain.service.org;

import java.util.List;

import lerrain.service.common.ServiceTools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 类EnterpriseService.java的实现描述：渠道服务
 * 
 * @author iyb-wangguangrong 2018年5月11日 上午10:48:25
 */
@Service
public class EnterpriseService {
    @Autowired
    EnterpriseDao enterpriseDao;
    @Autowired
    ServiceTools tools;


    public Long save(Enterprise enterprise) {
        // 主键ID与userId保持一致
        Long id = tools.nextId("enterprise");
        enterprise.setId(id);
        int result = enterpriseDao.save(enterprise);
        if (result > 0) {
            return id;
        }
        return null;
    }

    public List<Enterprise> querySubordinate(Long companyId) {
        return enterpriseDao.querySubordinate(companyId);
    }
}
