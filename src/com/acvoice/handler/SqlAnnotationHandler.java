package com.acvoice.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.acvoice.acorm.Operator;
import com.acvoice.acorm.OperatorType;
import com.acvoice.annotation.Delete;
import com.acvoice.annotation.Insert;
import com.acvoice.annotation.Parameter;
import com.acvoice.annotation.Select;
import com.acvoice.annotation.Table;
import com.acvoice.annotation.Update;

public class SqlAnnotationHandler {

	private static String isDelete1 = " and deleted = false";
	private static String isDelete2 = " where deleted = false ";
	
	public static List<Operator> getOperatos(Class<?> clazz){
		List<Operator> list = null;
		String tableName = getTableName(clazz);
		if(tableName==null){
			return null;
		}else{
			Method [] methods = clazz.getMethods();
			list = new ArrayList<Operator>();
			for (Method method : methods) {
				Operator operator = getOperator(tableName,method);
				list.add(operator);
			}
		} 
		return list;
	}
	public static String getTableName(Class<?> clazz){
		Table t = clazz.getAnnotation(Table.class);
		if(t==null){
			return null;
		}else{
			String tableName = t.value();
			return tableName;
		}
	}
	public static Operator getOperator(String tableName,Method method){
		Operator operator = new Operator();
		String where = " where ";
		Annotation[][] paramterAnnotations = method.getParameterAnnotations();
		int length = 0 ;
		for (Annotation[] annotations : paramterAnnotations) {
			for (Annotation annotation : annotations) {
				if(annotation instanceof Parameter){
					String value = ((Parameter) annotation).value();
					where = where + value + " =? and ";
					length++;
				}
			}
		}
		if(length == 0){
			where = null;
		}else{
			where = where.substring(0, where.lastIndexOf("and "));
		}
		operator.setWhere(where);
		operator.setTableName(tableName);
		operator.setWhere(where);
		operator.setId(method.getName());
		Class<?> returnType = method.getReturnType();
		if(returnType==List.class){
			ParameterizedType aType = (ParameterizedType) method.getGenericReturnType();
			Type t = aType.getActualTypeArguments()[0];
			operator.setReturnType((Class<?>) t);
		}else{
			operator.setReturnType(returnType);
		}
		Annotation [] annotations = method.getAnnotations();
		for (Annotation annotation : annotations) {
			if(annotation instanceof Select){
				operator.setType(OperatorType.SELECT);
				Select select = (Select) annotation;
				String value = select.value();
				operator.setSql(value);
				String where2 = operator.getWhere();
				if(where2==null||where.isEmpty()){
					operator.setWhere(isDelete2);
				}else{
					operator.setWhere(where2+isDelete1);
				}
				if(value.isEmpty()){
					operator.setSelfDefined(false);
				}else{
					operator.setSelfDefined(true);
				}
				
			}else if(annotation instanceof Insert){
				operator.setType(OperatorType.INSERT);
				Insert insert = (Insert) annotation;
				String value = insert.value();
				operator.setSql(value);
				if(value.isEmpty()){
					operator.setSelfDefined(false);
				}else{
					operator.setSelfDefined(true);
				}
			}else if(annotation instanceof Update){
				operator.setType(OperatorType.UPDATE);
				Update update = (Update) annotation;
				String value = update.value();
				operator.setSql(value);
				if(value.isEmpty()){
					operator.setSelfDefined(false);
				}else{
					operator.setSelfDefined(true);
				}
			}else if(annotation instanceof Delete){
				operator.setType(OperatorType.DELETE);
				Delete delete = (Delete) annotation;
				String value = delete.value();
				operator.setSql(value);
				if(value.isEmpty()){
					operator.setSelfDefined(false);
				}else{
					operator.setSelfDefined(true);
				}
			}
		}
		return operator;
	}
}
