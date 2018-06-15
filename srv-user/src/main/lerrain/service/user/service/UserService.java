package lerrain.service.user.service;

import java.util.Date;
import java.util.List;

import lerrain.service.user.Login;
import lerrain.service.user.User;
import lerrain.service.user.dao.UserDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService
{
	@Autowired
	UserDao userDao;

	public User login(String loginName, String password)
	{
		Long userId = userDao.verify(loginName, password);
		
		if (userId == null || "".equals(userId))
            throw new RuntimeException("用户不存在或密码错误");

		User user = userDao.load(userId);

		userDao.updateLoginTime(userId, new Date());

		return user;
	}
	
	public boolean verifyPassword(Long userId, String password)
	{
		return userDao.verify(userId, password) == 1;
	}

	public User getUser(Long userId)
	{
		return userDao.load(userId);
	}
	
	public boolean isExisted(String loginName)
	{
		return userDao.isExisted(loginName);
	}
	
	public String getUserId(String loginName)
	{
		return userDao.findUserId(loginName);
	}

	public void updateStatus(Long[] usersId, int status)
	{
		userDao.updateStatus(usersId, status);
	}
	
	public void updatePassword(Long[] usersId, String password)
	{
		userDao.updatePassword(usersId, password);
	}

	public void updatePassword(Long usersId, String password)
	{
		userDao.updatePassword(usersId, password);
	}

	public List<User> list(String search, int from, int num)
	{
		return userDao.list(search, from, num);
	}

	public int count(String search)
	{
		return userDao.count(search);
	}

    public boolean openAccount(Long userId, int userType, String loginName, String password, List<Long> roleIdList) {
        // 保存user信息
        User user = new User();
        user.setId(userId);
        user.setPassword(password);
        user.setType(userType);
        // 0-未激活 1-正常 9-锁定
        user.setStatus(1);
        userDao.save(user);
        // 保存登录信息
        Login login = new Login();
        login.setLoginName(loginName);
        login.setPassword(password);
        login.setStatus(1);
        login.setUserId(userId);
        userDao.saveLogin(login);
        // 保存用户角色
        for (Long roleId : roleIdList) {
            userDao.saveUserRole(userId, roleId);
        }
        return true;
    }

    public boolean removeLogin(String loginName) {
        return userDao.removeLogin(loginName) > 0;
    }

    public User csLogin(String loginName, String password) throws Exception {
        Long userId = userDao.verify(loginName, password);

        if (userId == null || "".equals(userId))
            throw new Exception("用户不存在或密码错误");

        User user = userDao.load(userId);

        userDao.updateLoginTime(userId, new Date());

        return user;
    }
}
