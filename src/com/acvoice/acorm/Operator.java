package com.acvoice.acorm;

public class Operator {

	private OperatorType type;
	private String id;
	private String where;
	private String sql;
	private String tableName;
	private boolean isSelfDefined;
	private Class<?> returnType;
	public Class<?> getReturnType() {
		return returnType;
	}
	public void setReturnType(Class<?> returnType) {
		this.returnType = returnType;
	}
	public boolean isSelfDefined() {
		return isSelfDefined;
	}
	public void setSelfDefined(boolean isSelfDefined) {
		this.isSelfDefined = isSelfDefined;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getSql() {
		return sql;
	}
	@Override
	public String toString() {
		return "Operator [type=" + type + ", id=" + id + ", where=" + where + ", sql=" + sql + ", tableName="
				+ tableName + ", isSelfDefined=" + isSelfDefined + "]";
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public OperatorType getType() {
		return type;
	}
	public void setType(OperatorType type) {
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getWhere() {
		return where;
	}
	public void setWhere(String where) {
		this.where = where;
	}

}
