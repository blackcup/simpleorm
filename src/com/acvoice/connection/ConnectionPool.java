package com.acvoice.connection;

import java.sql.Connection;

/**
 * @author zhao
 * ���ݿ����ӳ�
 * �ṩ�����ݿ����ӵĻ����ͷź�����
 */
public interface ConnectionPool {
	
	Connection getConnection();
	void realse(Connection connection);
	void destroy();
}