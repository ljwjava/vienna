package lerrain.service.org;

import java.util.ArrayList;
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
    
    public List<Enterprise> querySuperior(Long companyId) {
    	List<Enterprise> list = enterpriseDao.querySuperior(companyId);
    	List<Enterprise> result = new ArrayList<Enterprise>();
    	if(list != null && list.size() > 0) {
    		for(int i = list.size();i>0;i--) {
    			Enterprise e = list.get(i-1);
    			e.setLevel(list.size()-e.getLevel()-1);
    			result.add(e);
    		}
    	}
    	return result;
    }
    
    public Enterprise queryById(Long companyId) {
    	return enterpriseDao.queryById(companyId);
    }
    
    public int updateById(Enterprise enterprise) {
    	return enterpriseDao.updateById(enterprise);
    }
    
    public Long getCompanyId(Long userId) {
    	return enterpriseDao.getCompanyId(userId);
    }
}
