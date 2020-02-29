package com.bjhy.fbackup.client.core.domain;

/**
 * 目录类型(picture)
 * @author wubo
 */
public class DirectoryTypePicture {
	
	/**
	 * 静态资源的跟路径
	 */
	private String staticRootPath;
	
	/**
	 * 静态资源服务的url
	 */
	private String staticServiceUrl;
	
	/**
	 * 静态类型
	 */
	private String staticType;
	
	/**
	 * 数据库用户名
	 */
	private String databaseUsername;
	
	/**
	 * 数据库密码
	 */
	private String databasePassword;
	
	/**
	 * 数据库的url
	 */
	private String databaseUrl;
	
	/**
	 * 数据库驱动
	 */
	private String databaseDriver;

	public String getStaticRootPath() {
		return staticRootPath;
	}

	public void setStaticRootPath(String staticRootPath) {
		this.staticRootPath = staticRootPath;
	}

	public String getStaticServiceUrl() {
		return staticServiceUrl;
	}

	public void setStaticServiceUrl(String staticServiceUrl) {
		this.staticServiceUrl = staticServiceUrl;
	}

	public String getStaticType() {
		return staticType;
	}

	public void setStaticType(String staticType) {
		this.staticType = staticType;
	}

	public String getDatabaseUsername() {
		return databaseUsername;
	}

	public void setDatabaseUsername(String databaseUsername) {
		this.databaseUsername = databaseUsername;
	}

	public String getDatabasePassword() {
		return databasePassword;
	}

	public void setDatabasePassword(String databasePassword) {
		this.databasePassword = databasePassword;
	}

	public String getDatabaseDriver() {
		return databaseDriver;
	}

	public void setDatabaseDriver(String databaseDriver) {
		this.databaseDriver = databaseDriver;
	}

	public String getDatabaseUrl() {
		return databaseUrl;
	}

	public void setDatabaseUrl(String databaseUrl) {
		this.databaseUrl = databaseUrl;
	}
}
