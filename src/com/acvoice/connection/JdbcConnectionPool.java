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
 * @author IT民工
 * @time 2016.7.18
 * jdbc连接池
 */
public class JdbcConnectionPool implements ConnectionPool {
	private ConnectionProperties properties;
	volatile private  int  currentPoolSize;
	private   List<ComConnection> pool; //可用连接池
	private   List<Connection> usedPoll;//已用连接池
	private  ExecutorService  exc = Executors.newCachedThreadPool();
	private static   ConnectionPool connectionPool = null;
	/**
	 * 初始化连接池
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
	 * @return 单例模式获取连接池，注意同步
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
	 * 当前可以获取连接时即currentPoolSize<maxPoolSize
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	synchronized public Connection getConect() throws InterruptedException, ExecutionException{
		Connection con = null;
		if(pool.size()==0){//当前可用为0，并且还可以拓展
			Callable<Connection> c = new Callable<Connection>() {

				public Connection call() throws Exception {
					return getConnectionByManager();
				}	
				
			};
			for(int i = 0;i<3;i++){//尝试三次，获取，如果三次都没有获取到，就抛出异常
				Future<Connection> future= exc.submit(c);
				con = future.get();
				if(con!=null){
					usedPoll.add(con);
					currentPoolSize ++;
					return con;
				}
			}
			throw new RuntimeException("尝试了最大次数，并没有获得链接");
		}else{//不为0，可以从空闲列表中获取
			con = pool.get(pool.size()-1).getConnection();
			usedPoll.add(con);
			pool.remove(pool.size()-1);
			return con;
		}
	}
	/**
	 * @return
	 * @throws SQLException
	 * 从连接池manger中获取
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
				checkEquel();//启动一个现成去检测currentPoolSize与MaxPoolSize的关系
				wait();//等待，可以拓展
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
		pool.add(new ComConnection(connection, 0));//回收可用，并且重新计时
		usedPoll.remove(connection);//把已用连接池中的该连接的引用移除掉
		notify();
	}
	/**
	 * 每隔1分钟维护列表，把空闲时间比较长的连接释放掉，把不可用的连接也释放掉
	 */
	synchronized private void  checkOut(){
		Runnable r = new Runnable() {
			public void run() {
					try {
						for(int i=usedPoll.size()-1;i>=0;i--){
							try {
								//检测连接是否可用
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
								//检测连接是否可用
								if(pool.get(i).getConnection().isValid(100)){
								}else{
									pool.remove(i);
									currentPoolSize--;
								}
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						for(int i =pool.size()-1;i>=0;i--){//长时间空闲的释放掉
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
		//定时检测
		set.scheduleWithFixedDelay(r, 1, 1, TimeUnit.MINUTES);
	}
	/**
	 * 改线程检测currentPoolSize与maxPoolSize的关系，如果关系发生变化，就唤醒等待线程
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
	 * 该内部类用处存储当前Connection与空闲时间
	 * count为计时几次。
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
