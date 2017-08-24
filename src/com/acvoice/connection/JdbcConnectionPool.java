package com.acvoice.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * @author IT��
 * @time 2016.7.18
 * jdbc���ӳ�
 */
public class JdbcConnectionPool implements ConnectionPool {
	private ConnectionProperties properties;
	volatile private  int  currentPoolSize;
	private   List<ComConnection> pool; //�������ӳ�
	private   List<Connection> usedPoll;//�������ӳ�
	private  ExecutorService  exc = Executors.newCachedThreadPool();
	private static   ConnectionPool connectionPool = null;
	/**
	 * ��ʼ�����ӳ�
	 * @param pro
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private JdbcConnectionPool(ConnectionProperties pro) throws ClassNotFoundException, SQLException{
		this.properties = pro;
		pool = new ArrayList<ComConnection>(properties.getInitPoolSize());
		usedPoll = new ArrayList<Connection>();
			Class.forName(properties.getDriverClass());
			for(int i = 0;i<properties.getInitPoolSize();i++){
				Connection con =  getConnectionByManager();
				pool.add(new ComConnection(con, 0));
			}
		checkOut();
	} 
	/**
	 * @param pro
	 * @return ����ģʽ��ȡ���ӳأ�ע��ͬ��
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	synchronized public static ConnectionPool getConnnectionPool(ConnectionProperties pro) throws ClassNotFoundException, SQLException{
		if(connectionPool ==null){
			 connectionPool = new JdbcConnectionPool(pro);
		} 
		return connectionPool;
	}
	/**
	 * ��ǰ���Ի�ȡ����ʱ��currentPoolSize<maxPoolSize
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	synchronized public Connection getConect() throws InterruptedException, ExecutionException{
		Connection con = null;
		if(pool.size()==0){//��ǰ����Ϊ0�����һ�������չ
			Callable<Connection> c = new Callable<Connection>() {

				public Connection call() throws Exception {
					return getConnectionByManager();
				}	
				
			};
			for(int i = 0;i<3;i++){//�������Σ���ȡ��������ζ�û�л�ȡ�������׳��쳣
				Future<Connection> future= exc.submit(c);
				con = future.get();
				if(con!=null){
					usedPoll.add(con);
					currentPoolSize ++;
					return con;
				}
			}
			throw new RuntimeException("����������������û�л������");
		}else{//��Ϊ0�����Դӿ����б��л�ȡ
			con = pool.get(pool.size()-1).getConnection();
			usedPoll.add(con);
			pool.remove(pool.size()-1);
			return con;
		}
	}
	/**
	 * @return
	 * @throws SQLException
	 * �����ӳ�manger�л�ȡ
	 */
	private Connection getConnectionByManager() throws SQLException{
		Connection connection = DriverManager.getConnection(
				properties.getConnectionUrl(),properties.getUsername(),properties.getPassword());
		return connection;
	}
	/* (non-Javadoc)
	 * @see com.acvoice.ConnectionPool#getConnection()
	 */
	synchronized public  Connection  getConnection(){
		Connection con = null;
		try {
			if(currentPoolSize>=properties.getMaxPoolSize()&&pool.size()==0){
				checkEquel();//����һ���ֳ�ȥ���currentPoolSize��MaxPoolSize�Ĺ�ϵ
				wait();//�ȴ���������չ
				con = getConect();
			}else{
			con = getConect();
			}
			} catch (InterruptedException e) {
			e.printStackTrace();
			}catch (ExecutionException e) {
			e.printStackTrace();
		}
		return con;
	}	
	/* (non-Javadoc)
	 * @see com.acvoice.ConnectionPool#realse(java.sql.Connection)
	 */
	synchronized public void realse(Connection connection ){
		pool.add(new ComConnection(connection, 0));//���տ��ã��������¼�ʱ
		usedPoll.remove(connection);//���������ӳ��еĸ����ӵ������Ƴ���
		notify();
	}
	/**
	 * ÿ��1����ά���б��ѿ���ʱ��Ƚϳ��������ͷŵ����Ѳ����õ�����Ҳ�ͷŵ�
	 */
	synchronized private void  checkOut(){
		Runnable r = new Runnable() {
			public void run() {
					try {
						for(int i=usedPoll.size()-1;i>=0;i--){
							try {
								//��������Ƿ����
								if(usedPoll.get(i).isValid(100)){
								}else{
									usedPoll.remove(i);
									currentPoolSize--;
								
								}
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						for(int i=pool.size()-1;i>=0;i--){
							try {
								//��������Ƿ����
								if(pool.get(i).getConnection().isValid(100)){
								}else{
									pool.remove(i);
									currentPoolSize--;
								}
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						for(int i =pool.size()-1;i>=0;i--){//��ʱ����е��ͷŵ�
							ComConnection com = pool.get(i);
							if(pool.get(i).getCount()>properties.getDispoilTime()&&currentPoolSize>properties.getMinPoolSize()){	
								try {
									com.getConnection().close();
									pool.remove(i);
									com =null;
									currentPoolSize--;

								} catch (SQLException e) {
									e.printStackTrace();
								}
							}else{
								com.setCount(com.getCount()+1);
							}
						}
					} finally{
						
					}
					}
		};
		ScheduledExecutorService set = Executors.newScheduledThreadPool(1);
		//��ʱ���
		set.scheduleWithFixedDelay(r, 1, 1, TimeUnit.MINUTES);
	}
	/**
	 * ���̼߳��currentPoolSize��maxPoolSize�Ĺ�ϵ�������ϵ�����仯���ͻ��ѵȴ��߳�
	 */
	synchronized private void checkEquel(){
		
		Runnable r = new Runnable() {
			public void run() {
				while(currentPoolSize>=properties.getMaxPoolSize()){
				}
				notify();
			}
		};
		exc.execute(r);
	}
	/**
	 * @author zhao
	 * ���ڲ����ô��洢��ǰConnection�����ʱ��
	 * countΪ��ʱ���Ρ�
	 */
	private class ComConnection implements Comparable<ComConnection>{
		
		public Connection getConnection() {
			return connection;
		}
		public ComConnection(Connection connection, Integer count) {
			super();
			this.connection = connection;
			this.count = count;
		}
		public Integer getCount() {
			return count;
		}
		public void setCount(Integer count) {
			this.count = count;
		}
		Connection connection;
		Integer count;
		public int compareTo(ComConnection o) {
			if(o.getCount()>count)
				return -1;
			return 1;
		}
	}
	synchronized public void destroy() {
		for(int i=pool.size();i>=0;i--){
			try {
				pool.get(i).getConnection().close();
				pool.remove(i);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for(int i=usedPoll.size();i>=0;i--){
			try {
				usedPoll.get(i).close();
				usedPoll.remove(i);
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
}
}
