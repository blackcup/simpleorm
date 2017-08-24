package com.acvoice.acorm;

import java.util.ArrayList;
import java.util.List;
import com.acvoice.connection.ConnectionPool;
import com.acvoice.connection.ConnectionPoolManager;
import com.acvoice.handler.AcquireBeanHandler;
import com.acvoice.handler.AnnotationAcqurieBeanHandler;
import com.acvoice.handler.XmlAcquireBeanHandler;
public class SimpleOrm {

	private  SimpleOrmEnvironment environment = null;
	private  ConnectionPoolManager connectionManager;
	private String path;
	private List<AcquireBeanHandler> handlers= new ArrayList<AcquireBeanHandler>();
	public static SimpleOrm simpleOrm = null;
	public SimpleOrmEnvironment getEnvironment() {
		return environment;
	}
	private  SimpleOrm(String paths){
		this.path = paths;
		environment  = new SimpleOrmEnvironment(path);
		OrmConfig config = environment.getConfig();
		connectionManager = new ConnectionPoolManager(config.getDataSource().getType(),config.getDataSource().getProperties());
		/**这个如果在配置文件中配置了这个选项，则添加了一个获取目标对象的handler*/
		String mapperPath = config.getMapperPath();
		if(mapperPath!=null){
			handlers.add(new XmlAcquireBeanHandler(this));
		}
		handlers.add(new AnnotationAcqurieBeanHandler(this));
	}
	synchronized public static SimpleOrm getSimpleOrm(String paths){
		if(simpleOrm==null){
			simpleOrm = new SimpleOrm(paths);
		}
		return simpleOrm;
	}
	public Object getProxyObject(String className){
		for(AcquireBeanHandler handler : handlers){
			Object target = handler.getTargetBean(className);
			if(target!=null){
				return target;
			}
		}
		return null;
	}
	public ConnectionPool getPool() {
		/**默认是自己的连接池*/
		return connectionManager.getConnectionPool();
	}
	public ConnectionPoolManager getConnectionManager() {
		return connectionManager;
	}
	public void setConnectionManager(ConnectionPoolManager connectionManager) {
		this.connectionManager = connectionManager;
	}
}
