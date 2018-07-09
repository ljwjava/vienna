package lerrain.service.user.service;

import java.util.List;

import javax.annotation.Resource;

import lerrain.service.user.WxUser;
import lerrain.service.user.dao.WxUserDao;

import org.springframework.stereotype.Service;

@Service
public class WxUserService {

    @Resource
    WxUserDao wxUserDao;

    public Integer countWxUser(WxUser user) {
        return wxUserDao.countWxUser(user);
    }

    public List<WxUser> listWxUser(WxUser user) {
        return wxUserDao.listWxUser(user);
    }

    public void insert(WxUser user) {
        wxUserDao.insert(user);

    }

    public void updateByOpenId(WxUser user) {
        wxUserDao.updateByOpenId(user);
    }

}
