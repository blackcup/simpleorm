package com.acvoice.connection;

import java.sql.SQLException;

/**
 * @author zhao
 *对不同的数据库连接进行管理
 *对配置参数的不同的来源，形式以及数据库连接种类不同，提供统一的管理
 */
public class ConnectionPoolManager {
	/**数据库连接池，目前支持JdbcConnectionPool*/
	private ConnectionPool pool = null;
	/**配置参数加载器，用于加载后缀名为.xml或者。property等配置文件的配置*/
	private ConnectionPropertiesReader reader = null;
	/**数据库链接种类：JDBC or ODBC*/
	private ConnectionType type ;
	/**数据库链接参数*/
	private ConnectionProperties config;
	public ConnectionPoolManager() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ConnectionPoolManager(ConnectionType type2, ConnectionProperties property) {
		this.type = type2;
		config = property;
	}
	public ConnectionPoolManager(String typeString,ConnectionPropertiesReader reader){
		type = ConnectionType.valueOf(typeString.toUpperCase());
		this.reader = reader;
		initConfig();
	}
	public ConnectionPoolManager(ConnectionType type2, ConnectionPropertiesReader reader2) {
		this.type = type2;
		this.reader = reader2;
		initConfig();
	}
	public ConnectionPropertiesReader getReader() {
		return reader;
	}
	public void setReader(ConnectionPropertiesReader reader) {
		this.reader = reader;
	}
	public ConnectionPool getPool() {
		return pool;
	}
	public ConnectionType getType() {
		return type;
	}
	public ConnectionProperties getConfig() {
		return config;
	}

	public void  initConfig(){
		config = reader.getConnectionProperties();
	}

	public void setPool(ConnectionPool pool) {
		this.pool = pool;
	}
	public void setType(ConnectionType type) {
		this.type = type;
	}
	public void setConfig(ConnectionProperties config) {
		this.config = config;
	}

	public ConnectionPool getConnectionPool(){
		if(pool==null){
			pool = getConnectionPool(type);
		}
		return pool;
	}
	/**
	 * 根据连接类型不同，返回不同的数据库连接池
	 * @param type 
	 * @return
	 */
	private ConnectionPool getConnectionPool(ConnectionType type){
		try{
			switch (type) {
			case JDBC:{
				return JdbcConnectionPool.getConnnectionPool(config);
			}
			case ODBC:{
				return null;
			}
			default:
				return null;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			
		}
		return pool;
	}
	/**
	 * 对数据库连接池进行销毁，资源回收
	 */
	public void destroy(){
		
		if(pool!=null){
			pool.destroy();
		}
		pool = null;
	}
}
