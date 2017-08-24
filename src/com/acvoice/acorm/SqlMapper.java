package com.acvoice.acorm;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SqlMapper {

	private final static Map<String, List<Operator>> mapperContainor = new ConcurrentHashMap<String, List<Operator>>(64);
	public SqlMapper(String mapperPath2) {
		this.mapperPath = mapperPath2;
		
	}
	public Map<String, List<Operator>> getMappercontainor() {
		return mapperContainor;
	}
	private  String mapperPath;
	public  String getMapperPath() {
		return mapperPath;
	}
	public  void setMapperPath(String mapperPath) {
		this.mapperPath = mapperPath;
	}
}
