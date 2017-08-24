package com.acvoice.acorm;

import java.io.File;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.acvoice.connection.ConnectionPropertiesReader;
import com.acvoice.connection.ConnectionType;
import com.acvoice.connection.XmlConnectionPropertiesReader;
import com.acvoice.exception.NoSuchConnectionTypeException;

public class OrmConfigReader {

	private ConnectionPropertiesReader connectionReader;
	private String path;
	private SAXReader reader = new SAXReader();  
	private Document document;
	private String configType;
	public OrmConfig getConfig() {
		return config;
	}
	public void setConfig(OrmConfig config) {
		this.config = config;
	}
	private OrmConfig config ;
	public OrmConfigReader(String path,String configType){
		try {
			
			this.path = path;
			File f = new File(path);
			document = reader.read(f);
			this.configType = configType;
			
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
	}
	public void loadConfig() throws NoSuchConnectionTypeException{
		if(config!=null){
			Element e = (Element) document.selectSingleNode("//dataSource");
			
			Element typeNode =  (Element) e.selectSingleNode("//property[@id='connectionType']");
			
			String connectionType =typeNode.getText();
			ConnectionType type = ConnectionType.valueOf(connectionType.toUpperCase());
			DataSource source = new DataSource();
			source.setType(type);
			if(configType.equals("xml")){
				connectionReader = new XmlConnectionPropertiesReader(path);
				source.setProperties(connectionReader.getConnectionProperties());
			}
			config.setDataSource(source);
			config.setMapperPath(getMapperPath(e));
		}
	
	}
	private String  getMapperPath(Element e){
		Element n = (Element) e.selectSingleNode("//mapperPath");
		if(n!=null)
			return n.getText();
		return null;
	}
}
