package com.acvoice.acorm;

import com.acvoice.exception.NoSuchConnectionTypeException;

public class OrmConfig  {

	public  DataSource getDataSource() {
		return dataSource;
	}
	public  void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	public  String getMapperPath() {
		return mapperPath;
	}
	public  void setMapperPath(String mapperPath) {
		this.mapperPath = mapperPath;
	}
	public OrmConfigReader getReader() {
		return reader;
	}
	public void setReader(OrmConfigReader reader) {
		this.reader = reader;
	}
	private  DataSource dataSource;
	private  String mapperPath;
	private OrmConfigReader reader;
	public OrmConfig(String path ){
		reader = new OrmConfigReader(path, "xml");
		reader.setConfig(this);
		try {
			reader.loadConfig();
		} catch (NoSuchConnectionTypeException e) {
			throw new RuntimeException(e);  
		}
	}
}
