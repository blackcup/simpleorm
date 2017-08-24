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
		/**�������������ļ������������ѡ��������һ����ȡĿ������handler*/
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
		/**Ĭ�����Լ������ӳ�*/
		return connectionManager.getConnectionPool();
	}
	public ConnectionPoolManager getConnectionManager() {
		return connectionManager;
	}
	public void setConnectionManager(ConnectionPoolManager connectionManager) {
		this.connectionManager = connectionManager;
	}
}
