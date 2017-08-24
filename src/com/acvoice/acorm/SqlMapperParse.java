package com.acvoice.acorm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.acvoice.util.FileUtil;

public class SqlMapperParse {

	private SAXReader reader = new SAXReader();  
	private Document document;
	public SqlMapperParse(){
	}
	public void loadMapper(SqlMapper mapper){
		String path = mapper.getMapperPath();
		Map<String, List<Operator>> mapperContainor = mapper.getMappercontainor();
		loadMapper(path,mapperContainor);
	}
	private void loadMapper(String path, Map<String, List<Operator>> mapperContainor) {

		File f = new File(path);
		File [] xmlFiles = null;
		if(f.isDirectory()){
			xmlFiles = f.listFiles(FileUtil.getFilenameFilter("^.+\\.xml$"));
			if(xmlFiles!=null){
				loadMapper(xmlFiles,mapperContainor);
			}
		}else{
			loadMapper(f,mapperContainor);
		}
	}
	private void loadMapper(File[] xmlFiles, Map<String, List<Operator>> mapperContainor) {
		for(File file:xmlFiles){
			loadMapper(file,mapperContainor);
		}
	}
	private void loadMapper(File file, Map<String, List<Operator>> mapperContainor) {
		try {
			document = reader.read(file);
			@SuppressWarnings("rawtypes")
			List nodes = document.selectNodes("//classOperation");
			for(int i=0;i<nodes.size();i++){
				Element e = (Element) nodes.get(i);
				String className = e.attributeValue("class");
				String tablename = e.attributeValue("table");
				List<Operator> list = new ArrayList<Operator>();
				@SuppressWarnings("rawtypes")
				List operations  = e.selectNodes("//operation");
				for(int j=0;j<operations.size();j++){
					Element opElement = (Element) operations.get(j);
					Operator operator = new Operator();
					operator.setId(opElement.attributeValue("id"));
					String typeString = opElement.attributeValue("type");
					OperatorType type = OperatorType.valueOf(typeString.trim().toUpperCase());
					operator.setType(type);
					String v = opElement.attributeValue("selfDefined");//selfDefined the sql instead of auto-generated
					if(v!=null&&v.toUpperCase().equals("TRUE")){
						operator.setSql(opElement.getText().trim());
						operator.setSelfDefined(true);
					}else{
						operator.setWhere(opElement.getText().trim());
					}
					operator.setTableName(tablename);
					list.add(operator);
				}
				mapperContainor.put(className, list);
			}
		} catch (DocumentException e) {
		}
	}
}
