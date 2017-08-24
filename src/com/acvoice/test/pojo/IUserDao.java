package com.acvoice.test.pojo;

import java.util.List;

public interface IUserDao {
	
	public User findUserByUsename(String username);
	public List<User> findAllUser();
	public int addUser(User user);
	public int updateUser(User user,String username);
	public int deleteUser(String username);
	public User selectUser(String username);
	//public User findUserByUsename();
	
}
