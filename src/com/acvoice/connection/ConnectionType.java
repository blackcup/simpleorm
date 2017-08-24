package com.acvoice.connection;

/**
 * @author zhao
 * @time 2016.7.18
 */
public enum ConnectionType {

	JDBC("jdbc"),ODBC("odbc");
	private String value;
	private ConnectionType(String type){
		this.value = type.toUpperCase();
	}
	@Override
	public String toString() {
		return value;
	}
}
