package com.acvoice.acorm;

import com.acvoice.connection.ConnectionProperties;
import com.acvoice.connection.ConnectionType;

public class DataSource {

	private ConnectionType type;
	private ConnectionProperties properties;
	public ConnectionType getType() {
		return type;
	}
	public void setType(ConnectionType type) {
		this.type = type;
	}
	public ConnectionProperties getProperties() {
		return properties;
	}
	public void setProperties(ConnectionProperties properties) {
		this.properties = properties;
	}
	
}
