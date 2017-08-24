package com.acvoice.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BasicTypeUtil {

	public static boolean isWraperType( Class<?> clazz){
		if(clazz==String.class)
			return true;
		if(clazz==Integer.class)
			return true;
		if(clazz==Long.class)
			return true;
		if(clazz==Double.class)
			return true;
		if(clazz==Float.class)
			return true;
		if(clazz==Character.class)
			return true;
		if(clazz==Boolean.class)
			return true;
		if(clazz==Byte.class)
			return true;	
		if(clazz==Short.class)
			return true;	
		if(clazz==Date.class)
			return true;	
		return false;
	}
	public static boolean isWraperType(Object obj){
		if(obj instanceof String)
			return true;
		if(obj instanceof Integer)
			return true;
		if(obj instanceof Long)
			return true;
		if(obj instanceof Double)
			return true;
		if(obj instanceof Short)
			return true;
		if(obj instanceof Float)
			return true;
		if(obj instanceof Character)
			return true;
		if(obj instanceof Boolean)
			return true;
		if(obj instanceof Byte)
			return true;
		if(obj instanceof Date)
			return true;
		return false;
	}
	public static boolean isBasicType( Class<?> clazz){
		if(clazz==int.class)
			return true;
		if(clazz==long.class)
			return true;
		if(clazz==double.class)
			return true;
		if(clazz==float.class)
			return true;
		if(clazz==char.class)
			return true;
		if(clazz==boolean.class)
			return true;
		if(clazz==byte.class)
			return true;	
		if(clazz==short.class)
			return true;	
		return false;
	}
	public static boolean isBasicType(String type ){
		if(type.equals("int"))
			return true;
		if(type.equals("long"))
			return true;
		if(type.equals("double"))
			return true;
		if(type.equals("float"))
			return true;
		if(type.equals("char"))
			return true;
		if(type.equals("boolean"))
			return true;
		if(type.equals("byte"))
			return true;
		if(type.equals("short"))
			return true;
		return false;
	}
	public static  boolean isBasicOrWrapperType(Class<?>type){
		return (isWraperType(type)||isBasicType(type));
	}
	public static boolean isNumber(Class<?> clazz){
		if(clazz==Integer.class||clazz==int.class){
			return true;
		}
		if(clazz==Double.class||clazz==double.class){
			return true;
		}
		if(clazz==Long.class||clazz==long.class){
			return true;
		}
		if(clazz==Short.class||clazz==short.class){
			return true;
		}
		if(clazz==Float.class||clazz==float.class){
			return true;
		}
		if(clazz==Byte.class||clazz==byte.class){
			return true;
		}
		return false;
	}
	public static Class<?> getWrapperType(Class<?> clazz) {
		if(clazz==int.class)
			return Integer.class;
		if(clazz==long.class)
			return Long.class;
		if(clazz==double.class)
			return Double.class;
		if(clazz==float.class)
			return Float.class;
		if(clazz==char.class)
			return Character.class;
		if(clazz==boolean.class)
			return Boolean.class;
		if(clazz==byte.class)
			return Byte.class;	
		if(clazz==short.class)
			return Short.class;	
		return clazz;
	}
	@SuppressWarnings("unchecked")
	public static <T> T getBasicBean(Class<T> clazz,String tar){
		if(clazz==Date.class){
			String []patters = new String[]{"yyyyƒÍMM‘¬dd»’","yyyy/MM/dd","yyyy-MM-dd"
					};
			for(String s : patters){
				SimpleDateFormat sdFormat = new SimpleDateFormat(s);
				try {
					Date parse = sdFormat.parse(tar);
					return (T) parse;
				} catch (Exception e) {
					continue;
				}
				
			}
		}else{
			try {
				Constructor<T> constructor = clazz.getConstructor(String.class);
				return constructor.newInstance(tar);
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
		}
		return null;
	}
	public static Object getDefaultValue(Class<?> clazz){

		if(isNumber(clazz))
			return -1;
		if(clazz==boolean.class||clazz==Boolean.class){
			return false;
		}
		if(clazz==char.class||clazz==Character.class){
			return '\0';
		}
		return null;
	}
	
}
