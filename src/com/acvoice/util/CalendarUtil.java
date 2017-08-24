package com.acvoice.util;

import java.util.Calendar;

public class CalendarUtil {

	public static int getCurrentPeriod(){
		Calendar instance = Calendar.getInstance();
		int i = instance.get(Calendar.MONTH)+1;
		if((i>=8&&i<=12)||i==1){
			return 1;
		}else{
			return 2;
		}
		
	}
	public static int getYear(){
		Calendar instance = Calendar.getInstance();
		return instance.get(Calendar.YEAR);
	}
}
