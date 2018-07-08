package lerrain.service.user.service;

import lerrain.service.user.AppUser;
import lerrain.service.user.dao.AppDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppService
{
    @Autowired
    AppDao appDao;

    public AppUser find(String userKey, String appCode)
    {
        AppUser appUser = appDao.find(userKey, appCode);
        return appUser != null ? appUser : appDao.newUser(userKey, appCode);
    }
}
