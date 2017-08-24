package com.acvoice.connection;

import java.sql.SQLException;

/**
 * @author zhao
 *�Բ�ͬ�����ݿ����ӽ��й���
 *�����ò����Ĳ�ͬ����Դ����ʽ�Լ����ݿ��������಻ͬ���ṩͳһ�Ĺ���
 */
public class ConnectionPoolManager {
	/**���ݿ����ӳأ�Ŀǰ֧��JdbcConnectionPool*/
	private ConnectionPool pool = null;
	/**���ò��������������ڼ��غ�׺��Ϊ.xml���ߡ�property�������ļ�������*/
	private ConnectionPropertiesReader reader = null;
	/**���ݿ��������ࣺJDBC or ODBC*/
	private ConnectionType type ;
	/**���ݿ����Ӳ���*/
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
	 * �����������Ͳ�ͬ�����ز�ͬ�����ݿ����ӳ�
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
	 * �����ݿ����ӳؽ������٣���Դ����
	 */
	public void destroy(){
		
		if(pool!=null){
			pool.destroy();
		}
		pool = null;
	}
}
