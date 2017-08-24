package com.acvoice.daodemo;

import com.acvoice.annotation.Delete;
import com.acvoice.annotation.Insert;
import com.acvoice.annotation.Parameter;
import com.acvoice.annotation.Select;
import com.acvoice.annotation.Table;
import com.acvoice.annotation.Update;
import com.acvoice.test.pojo.User;

@Table("User")
public interface IUserDao {

	/**
	 * @param username
	 * @return
	 */
	@Select
	public User findUserByUsename(@Parameter("username") String username,@Parameter("id") int id);
	@Insert
	public User addUser(User user);
	@Update
	public int updateUser(User user,@Parameter("username") String username);
	@Delete 
	public int deleteUser(@Parameter("username") String username);
}
