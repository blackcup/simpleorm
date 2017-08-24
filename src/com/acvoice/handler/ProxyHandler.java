package com.acvoice.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import com.acvoice.acorm.Operator;
import com.acvoice.acorm.OperatorType;
import com.acvoice.connection.ConnectionPool;
import com.acvoice.simpleorm.util.SqlUtil;

public class ProxyHandler implements InvocationHandler{

	public ConnectionPool getPool() {
		return pool;
	}
	private List<Operator>methodInfo;
	public void setPool(ConnectionPool pool) {
		this.pool = pool;
	}
	private ConnectionPool pool;
	public ProxyHandler(ConnectionPool pool,List<Operator>methodInfo) {
		super();
		this.pool = pool;
		this.methodInfo = methodInfo;
	}
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		String methodName = method.getName();
		if(methodName.equals("toString")){
			return super.toString();
		}else if(methodName.equals("equals")){
			return super.equals(args[0]);
		}else if(methodName.equals("hashCode")){
			return super.hashCode();
		}else{
			for(Operator operator : methodInfo){
				if(operator.getId().equals(methodName)){
					String sql;
					if(args==null){
						sql= SqlUtil.getSqlString(operator,null);
					}else{
						sql= SqlUtil.getSqlString(operator,args[0]);
					}
					
					System.out.println(sql);
					Connection connection = null;
					try{
						connection = pool.getConnection();
						connection.setAutoCommit(false);
						PreparedStatement  psttm = connection.prepareStatement(sql);
						OperatorType type = operator.getType();
						//这里定义update的第一个参数为实体，后面为其他参数
						if(type==OperatorType.UPDATE){
							for(int i =1 ;i<args.length;i++){
								psttm.setObject(i, args[i]);
							}
						}else if(type==OperatorType.INSERT){
							
						}else{
							if(args!=null)
								for(int i =0 ;i<args.length;i++){
									psttm.setObject(i+1, args[i]);
								}
						}
						if(type==OperatorType.SELECT){
							ResultSet set = psttm.executeQuery();
							connection.commit();
							Class<?> returnType = method.getReturnType();
							if(returnType==List.class){
								ParameterizedType aType = (ParameterizedType) method.getGenericReturnType();
								Type t = aType.getActualTypeArguments()[0];
								return SqlUtil.ConvertToList(set, (Class<?>) t);
							}else{
								return SqlUtil.ConvertToBean(set, returnType);
							}
						}else{
							int affected = psttm.executeUpdate();
							connection.commit();
							return affected;
						}
					}catch(Exception e){
						e.printStackTrace();
						connection.rollback();
						return null;
					}
					finally {
						pool.realse(connection);
					}
				}
			}
		}
		return null;
	}

}
