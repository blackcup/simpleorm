package com.acvoice.test.pojo;

public class Student {

	private String name;
	private Double height;
	public String getName() {
		return name;
	}
	public Student() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Student(String name, Double height) {
		super();
		this.name = name;
		this.height = height;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getHeight() {
		return height;
	}
	public void setHeight(Double height) {
		this.height = height;
	}
}
