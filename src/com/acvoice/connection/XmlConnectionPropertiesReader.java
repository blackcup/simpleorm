package com.acvoice.connection;

import java.io.File;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class XmlConnectionPropertiesReader implements ConnectionPropertiesReader{

	private String path;
	private SAXReader reader = new SAXReader();  
	Document document;
	public XmlConnectionPropertiesReader(String path)  {
		this.path = path;
	    try {
			document= reader.read(new File(path));
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new RuntimeException("can not find the connection property file");
		}
	}
	/* (non-Javadoc)
	 * @see com.acvoice.ConnectionPropertyReader#getConnectionProperty()
	 */
	public ConnectionProperties getConnectionProperties(){
		ConnectionProperties connectionProperties = new ConnectionProperties();
		Element root = (Element) document.selectSingleNode("//dataSource");
		connectionProperties.setConnectionType(ConnectionType.JDBC);
		Node node = root.selectSingleNode("//property[@id='username']");
		connectionProperties.setUsername(node.getText());
		node = root.selectSingleNode("//property[@id='password']");
		connectionProperties.setPassword(node.getText());
		node = root.selectSingleNode("//property[@id='jdbcUrl']");
		connectionProperties.setConnectionUrl(node.getText());
		node = root.selectSingleNode("//property[@id='driverClass']");
		connectionProperties.setDriverClass(node.getText().trim());
		node = root.selectSingleNode("//property[@id='minPoolSize']");
		if(node!=null){
			connectionProperties.setMinPoolSize(Integer.parseInt(node.getText()));
		}
		node = root.selectSingleNode("//property[@id='maxPoolSize']");
		if(node!=null){
			connectionProperties.setMaxPoolSize(Integer.parseInt(node.getText()));
		}
		node = root.selectSingleNode("//property[@id='initPoolSize']");
		if(node!=null){
			connectionProperties.setInitPoolSize(Integer.parseInt(node.getText()));
		}else{
			connectionProperties.setInitPoolSize((connectionProperties.getMinPoolSize()+connectionProperties.getMaxPoolSize()/2));
		}
		node = root.selectSingleNode("//property[@id='dispoilTime']");
		if(node!=null){
			connectionProperties.setDispoilTime(Integer.parseInt(node.getText()));
		}
		return connectionProperties;
	}
}
