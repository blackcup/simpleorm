package com.acvoice.test.pojo;

public class User {

	
	String username ;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Integer getAge() {
		return age;
	}
	@Override
	public String toString() {
		return "User [username=" + username + ", age=" + age + "]";
	}
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	public User(String username, Integer age) {
		super();
		this.username = username;
		this.age = age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	Integer age ;
}
