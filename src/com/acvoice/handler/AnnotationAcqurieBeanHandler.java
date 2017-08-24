package com.acvoice.handler;

import java.lang.reflect.Proxy;
import java.util.List;

import com.acvoice.acorm.Operator;
import com.acvoice.acorm.SimpleOrm;

public class AnnotationAcqurieBeanHandler implements AcquireBeanHandler{

	private SimpleOrm orm ; 
	public AnnotationAcqurieBeanHandler(SimpleOrm orm){
		this.orm = orm;
	}
	public Object getTargetBean(String className) {
		try {
			Class<?> clazz = Class.forName(className);
			if(!clazz.isInterface()){
				return null;
			}
			List<Operator> list = SqlAnnotationHandler.getOperatos(clazz);
			if(list==null||list.size()==0){
				return null;
			}
			ProxyHandler handler = new ProxyHandler(orm.getPool(), list);
			Object target = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, handler);
			return target;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

}
