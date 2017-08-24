package com.acvoice.util;

import java.util.Random;

public class RandomUtil {

	private static Random random = new Random();
	public static int getInteRandomNumber(){
		return random.nextInt(10000);
	}
}
