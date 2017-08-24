package com.acvoice.connection;

import java.sql.Connection;

/**
 * @author zhao
 * 数据库连接池
 * 提供对数据库连接的缓存释放和消耗
 */
public interface ConnectionPool {
	
	Connection getConnection();
	void realse(Connection connection);
	void destroy();
}