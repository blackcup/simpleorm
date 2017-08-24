package com.acvoice.simpleorm.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.acvoice.acorm.Operator;
import com.acvoice.util.BasicTypeUtil;
public class SqlUtil {
	public static String getUpdateSql(String tableName,Object obj,String where){
		try {
			Class<? extends Object> clzz = obj.getClass();
			String update = "Update "+ tableName + " Set ";
			for(Field field:clzz.getDeclaredFields()){
				field.setAccessible(true);
				Object va= field.get(obj);
				if(va!=null&&BasicTypeUtil.isBasicOrWrapperType(field.getType())){
					if(BasicTypeUtil.isNumber(field.getType())){
						update = update + field.getName() + "=" + va +",";
					}else{
						update = update + field.getName() + "='" + va +"',";
					}
				}
			}
			update = update.substring(0, update.lastIndexOf(","));
			return update + " " + where;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
		
	}

	public static String getDeleteSql(String tableName,String where){
		String select = "update "+tableName + " set deleted = true" + where+ " ";
		return select;
	}
	public static String getSelectSql(String tableName,Class<?> clazz, String where){
			StringBuilder sb = new StringBuilder("select ");
			Field[] declaredFields = clazz.getDeclaredFields();
			for (Field field : declaredFields) {
				sb.append(field.getName()).append(",");
			}
			String sql = sb.toString();
			sql = sql.substring(0, sql.lastIndexOf(",")) + " from "+ tableName + where;
			return sql;
		}
	public static String getInsertBulkSql(String tableName,Object obj) throws IllegalArgumentException, IllegalAccessException{
		List<?> list = (List<?>) obj;
		Class<?> targetClass = null;
		if(list.isEmpty()){
			return null;
		}else{
			for (Object object : list) {//获取list集合中一个不为空的元素，然后获取他的实际类型
				if(object!=null){
					targetClass = object.getClass();
					if(BasicTypeUtil.isWraperType(targetClass)){//如果元素类型为基本类型，则放弃
						targetClass = null;
						continue;
					}
					break;
				}
			}
			if(targetClass!=null){//获取到实际类型
				String insert = "insert into "+ tableName + " (";
				String valuesString = "values ";
				StringBuilder [] builders = new StringBuilder[list.size()];
				for(int i = 0;i<builders.length;i++){
					builders[i] = new StringBuilder("(");
				}
				Field []fields = targetClass.getDeclaredFields();
				for(int k = 0;k<fields.length;k++){
					fields[k].setAccessible(true);
					if(BasicTypeUtil.isBasicOrWrapperType(fields[k].getType())){
						insert = insert + fields[k].getName() + "," ;
					}
					if(BasicTypeUtil.isNumber(fields[k].getType())){
						for (int i = 0; i < builders.length; i++) {
							Object value = fields[k].get(list.get(i));
							if(value==null){
								builders[i].append("null");
							}else{
								builders[i].append(value.toString());
							}
							if(k==fields.length-1){
								builders[i].append(")");
							}else{
								builders[i].append(",");
							}
						}
					}else{
						for (int i = 0; i < builders.length; i++) {
							Object value = fields[k].get(list.get(i));
							if(value==null){
								builders[i].append("null");
							}else{
								builders[i].append("'");
								builders[i].append(value.toString());
								builders[i].append("'");
							}
							if(k==fields.length-1){
								builders[i].append(")");
							}else{
								builders[i].append(",");
							}
						}
					}
				}
				StringBuilder sb = new StringBuilder("");
				for(int i = 0;i<builders.length;i++){
					sb.append(builders[i].toString());
					if(i<builders.length-1)
						sb.append(",");
				}
				insert = insert.substring(0, insert.lastIndexOf(",")) + ")";//insert 语句完成
				valuesString = valuesString + sb.toString();//values 
				return insert + " " + valuesString + " "; //return sql
			}else{//没有，就返回空
				return null;
			}
			
		}
	}
	public static String getInsertSql(String tableName,Object obj){
		try {
			Class<?> clzz = obj.getClass();
			if(obj instanceof List){
				return getInsertBulkSql(tableName,obj);
			}
			String insert = "insert into "+ tableName + " (";
			String value = "values (";
			for(Field field:clzz.getDeclaredFields()){
				field.setAccessible(true);
				Object o = field.get(obj);
				if(o!=null&&BasicTypeUtil.isBasicOrWrapperType(field.getType())){
					if(BasicTypeUtil.isNumber(field.getType())){
						insert = insert + field.getName() + "," ;
						value = value + o+",";
					}else{
						insert = insert + field.getName() + "," ;
						value = value + "'"+ o +"',";
					}
				}
			}
			insert = insert.substring(0, insert.lastIndexOf(",")) + ")";
			value = value.substring(0, value.lastIndexOf(",")) + ")";
			return insert + "  "+value;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		}
	public static String getSqlString(Operator o,Object obj){
		if(!o.isSelfDefined()){
			switch (o.getType()) {
			case SELECT:
				return getSelectSql(o.getTableName(),o.getReturnType(), o.getWhere());
			case INSERT:
				return getInsertSql(o.getTableName(), obj);
			case UPDATE:
				return getUpdateSql(o.getTableName(), obj, o.getWhere());
			case DELETE:
				return getDeleteSql(o.getTableName(), o.getWhere());
			}
		}else{
			return o.getSql();
		}
		
		return null;
	}
	@SuppressWarnings("unchecked")
	public static <T> T ConvertToBean(ResultSet set,Class<T> clazz){
		try {
			if(!set.next()){
				return null;
			}
			ResultSetMetaData meta = set.getMetaData();
			int count = meta.getColumnCount();
			if(BasicTypeUtil.isBasicType(clazz)){
				Class<?> cl = BasicTypeUtil.getWrapperType(clazz);
				Constructor<?> c = cl.getConstructor(String.class);
				Object o = c.newInstance(set.getObject(1).toString());
				return (T) o;
			}else if(BasicTypeUtil.isWraperType(clazz)){
				Constructor<T> c = clazz.getConstructor(String.class);
				Object o = c.newInstance(set.getObject(1).toString());
				return (T) o;
			}else{
				Object des = clazz.newInstance();
				for(int i=0;i<count;i++){
					String name = meta.getColumnName(i+1);
					Object value = set.getObject(i+1);
					try {
						Field field = clazz.getDeclaredField(name);
						field.setAccessible(true);
						field.set(des, value);
					} catch (NoSuchFieldException e) {
						
					}
				}
				return (T) des;
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static <T> List<T> ConvertToList(ResultSet set,Class<T> clazz){
		try{
			List<T> list = new ArrayList<T>();
			while(set.next()){
				ResultSetMetaData meta = set.getMetaData();
				int count = meta.getColumnCount();
				if(BasicTypeUtil.isBasicType(clazz)){
					Class<?> cl = BasicTypeUtil.getWrapperType(clazz);
					Constructor<?> c = cl.getConstructor(String.class);
					@SuppressWarnings("unchecked")
					T des = (T) c.newInstance(set.getObject(0).toString());
					list.add(des);
				}else if(BasicTypeUtil.isWraperType(clazz)){
					Constructor<T> c = clazz.getConstructor(String.class);
					T des = (T) c.newInstance(set.getObject(0).toString());
					list.add(des);
				}else{
					T des = (T) clazz.newInstance();
					for(int i=0;i<count;i++){
						String name = meta.getColumnName(i+1);
						Object value = set.getObject(i+1);
						try {
							Field field = clazz.getDeclaredField(name);
							field.setAccessible(true);
							field.set(des, value);
						} catch (NoSuchFieldException e) {
							
						}
					}
					list.add(des);
				}
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public static <T> T [] ConvertToArray(ResultSet set,Class<T> clazz){
		List<?> list = ConvertToList(set,clazz);
		T[] objs =  (T[]) new Object[list.size()];
		for(int i = 0;i<list.size();i++){
			objs[i] = (T) list.get(i);
		}
		return objs;
	}
	
}
