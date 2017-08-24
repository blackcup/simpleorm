package com.acvoice.acorm;


public class SimpleOrmEnvironment {

	private  String configPath;
	private  OrmConfig config;
	
	public OrmConfig getConfig() {
		if(config==null&&configPath!=null){
			config = new OrmConfig(configPath); 
		}
		return config;
	}
	public String getConfigPath() {
		return configPath;
	}
	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}
	public SimpleOrmEnvironment(){}
	public SimpleOrmEnvironment(String configPath){
		this.configPath = configPath;
	}
}
