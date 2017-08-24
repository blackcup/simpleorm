package com.acvoice.util;

import java.util.ArrayList;
import java.util.List;

public class BeanUtil {
	public static String convertObjectToString(Object target){
		if(BasicTypeUtil.isBasicOrWrapperType(target.getClass())){
			return target.toString();
		}else{
			return ConvertBeanToString(target);
		}
	}
	public static  Object getDefaultValue(Class<?> clazz){
		if(BasicTypeUtil.isBasicOrWrapperType(clazz)){
			return BasicTypeUtil.getDefaultValue(clazz);
		}
		return null;
	}
	public static String ConvertBeanToString(Object target) {
		return null;
	}
	public static Object resolveObject() {
		return null;
	}
	public static List<String> convertObjectToString(Object []targets){
		if(targets==null||targets.length==0){
			return null;
		}else{
			List<String> list = new ArrayList<String>();
			for (Object object : targets) {
				String string = convertObjectToString(object);
				list.add(string);
			}
			return list;
		}
	}
	public static Object convertStringToObject(Class<?> clazz,String beanString){
		if(beanString==null||"".equals(beanString)){
			return null;
		}
		if(BasicTypeUtil.isBasicOrWrapperType(clazz)){
			Class<?> wrapperType = BasicTypeUtil.getWrapperType(clazz);
			Object basicBean = BasicTypeUtil.getBasicBean(wrapperType, beanString);
			return basicBean;
		}else{
			return convertStringToBean(clazz,beanString);
		}
	}
	public static Object convertStringToBean(Class<?> clazz, String beanString) {
		return null;
	}
}
