package com.acvoice.util;

import java.io.File;
import java.io.FilenameFilter;
public class FileUtil {

	public static String getRelativePathByPackage(String packname){
		return packname.replaceAll("\\.", "/");
	}
	public static String getAbsolutePathByPackage(){
		String path = ClassLoader.getSystemResource("").getPath();
		return path;
		
	}


	public static FilenameFilter getFilenameFilter(final String regx){
		return new FilenameFilter(){
			public boolean accept(File dir, String name) {
				if(name.matches(regx))
					return true;
				return false;
			}
			
		};
	}
	public static String getFileName(String filePath,String fileName){
		File file = new File(filePath);
		if(file.exists()){
			String type = fileName.substring(fileName.lastIndexOf("."), fileName.length());
			fileName = fileName.substring(0, fileName.lastIndexOf("."))+RandomUtil.getInteRandomNumber()+type;
		}
		return fileName;
	}
}
