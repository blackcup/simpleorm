package com.acvoice.util;

import java.util.Iterator;
import java.util.LinkedHashMap;

import org.junit.Test;

public class TestDemo {

		@Test
		public void testDelete2(){
			LinkedHashMap<String,Integer> hashMap = new LinkedHashMap<String,Integer>();
			hashMap.put("xiaoming", 10);
			hashMap.put("wangyang", 20);
			hashMap.put("chendong", 60);
			Iterator<String> iterator = hashMap.keySet().iterator();
			while(iterator.hasNext()){
				String key=iterator.next();
				if(key.equals("wangyang"));
				{
					iterator.remove();
				}
			}
		
			System.out.println(hashMap);

		}

}