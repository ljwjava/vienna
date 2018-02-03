package lerrain.service.user.service;

import lerrain.service.user.User;
import lerrain.service.user.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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
}
