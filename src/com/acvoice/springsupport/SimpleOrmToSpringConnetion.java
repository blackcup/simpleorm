package com.acvoice.springsupport;

import java.lang.reflect.Field;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.acvoice.acorm.SimpleOrm;
import com.acvoice.annotation.AcAutowired;

public class SimpleOrmToSpringConnetion implements BeanPostProcessor{

	private SimpleOrm orm ;
	public String getConfigLocation() {
		return configLocation;
	}
	public String getBasePackage() {
		return basePackage;
	}
	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}
	public String basePackage;
	public void setConfigLocation(String configLocation) {
		String src = this.getClass().getClassLoader().getResource("/").getPath();
		this.configLocation = src+configLocation;
	}
	private String configLocation;
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		Class<?> beanClass = bean.getClass();
		String packageString = beanClass.getPackage().getName();
		if(!basePackage.equals(packageString)){
			return bean; 
		}
		Field []fields = bean.getClass().getDeclaredFields();
		for (Field field : fields) {
			AcAutowired autowired = field.getAnnotation(AcAutowired.class);
			 if(autowired!=null){
				 Class<?> clazz = field.getType();
				 if(orm==null){
					 orm = SimpleOrm.getSimpleOrm(configLocation);
				 }
				 Object fieldBean = orm.getProxyObject(clazz.getName());
				 try {
					 field.setAccessible(true);
					field.set(bean, fieldBean);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			 }
		}

		return bean;
	}
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
