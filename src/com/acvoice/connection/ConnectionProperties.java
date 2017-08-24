package com.acvoice.connection;

/**
 * @author zhao
 * the class to load the  configration of connetion pool
 */
public class ConnectionProperties {

	/** the default max_size of connetion*/
	private final static int DEFAULT_MAXPOOLSIZE = 20; 
	/** the default min_size of connetion*/
	private final static int DEFAULT_MINPOOLSIZE = 10;
	/**if a connetion never been used last for DEFAULT_DISPOILTIME ,
	 * this connetion will be recalled by pool*/
	private final static int DEFAULT_DISPOILTIME = 3;
	/** the default data access way :JDBC or ODBC*/
	private final static ConnectionType DEFAULT_CONNETIONTYPE = ConnectionType.JDBC;
	private  int minPoolSize;
	private  int maxPoolSize;
	private  int initPoolSize;
	private  String username ;
	private  String password;
	/** the string of connection  */
	private  String connectionUrl;
	/**the class to drive the data access*/
	private String driverClass;
	private ConnectionType connectionType ;
	public ConnectionProperties() {
		super();
		maxPoolSize = DEFAULT_MAXPOOLSIZE;
		minPoolSize = DEFAULT_MINPOOLSIZE;
		initPoolSize = getDefaultInitPoolSize();
		dispoilTime = DEFAULT_DISPOILTIME;
		connectionType =DEFAULT_CONNETIONTYPE;
	}
	public ConnectionType getConnectionType() {
		return connectionType;
	}
	public void setConnectionType(ConnectionType connectionType) {
		this.connectionType = connectionType;
	}
	/**
	 * if initPoolSize did not be configurated ,this default method will be applied  
	 * @return
	 */
	private int getDefaultInitPoolSize(){
		return (maxPoolSize+minPoolSize)/2;
	}
	public String getDriverClass() {
		return driverClass;
	}
	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}
	private  int dispoilTime ;
	public int getMinPoolSize() {
		return minPoolSize;
	}
	public void setMinPoolSize(int minPoolSize) {
		this.minPoolSize = minPoolSize;
	}
	public int getMaxPoolSize() {
		return maxPoolSize;
	}
	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}
	public int getInitPoolSize() {
		return initPoolSize;
	}
	public void setInitPoolSize(int initPoolSize) {
		this.initPoolSize = initPoolSize;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getConnectionUrl() {
		return connectionUrl;
	}
	public void setConnectionUrl(String connectionUrl) {
		this.connectionUrl = connectionUrl;
	}
	public int getDispoilTime() {
		return dispoilTime;
	}
	public void setDispoilTime(int dispoilTime) {
		this.dispoilTime = dispoilTime;
	}
}
