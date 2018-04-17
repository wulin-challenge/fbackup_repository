package com.bjhy.fbackup.common.domain;

/**
 * 版本
 * @author wubo
 */
public class Version {
	
	/**
	 * 客户端的静态资源版本
	 */
	private String versionStaticsPicture;
	
	/**
	 * 客户端的数据库备份文件版本
	 */
	private String versionDatabaseFile;

	public String getVersionStaticsPicture() {
		return versionStaticsPicture;
	}

	public void setVersionStaticsPicture(String versionStaticsPicture) {
		this.versionStaticsPicture = versionStaticsPicture;
	}

	public String getVersionDatabaseFile() {
		return versionDatabaseFile;
	}

	public void setVersionDatabaseFile(String versionDatabaseFile) {
		this.versionDatabaseFile = versionDatabaseFile;
	}
}
