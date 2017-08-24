package com.acvoice.handler;

import java.lang.reflect.Proxy;
import java.util.List;

import com.acvoice.acorm.Operator;
import com.acvoice.acorm.SimpleOrm;
import com.acvoice.acorm.SqlMapper;
import com.acvoice.acorm.SqlMapperParse;

public class XmlAcquireBeanHandler implements AcquireBeanHandler{

	private  SqlMapper mapper;
	private SimpleOrm orm;
	public  XmlAcquireBeanHandler(SimpleOrm orm ){
		this.orm = orm;
		String acturalPath = this.getClass().getResource("/").getPath()+orm.getEnvironment().getConfig().getMapperPath();
		mapper = new SqlMapper(acturalPath);
		SqlMapperParse parse = new SqlMapperParse();
		parse.loadMapper(mapper);
	}
	public Object getTargetBean(String className) {

		try {
			Class<?> clazz = Class.forName(className);
			if(!clazz.isInterface()){
				return null;
			}
			List<Operator> list = mapper.getMappercontainor().get(className);
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
