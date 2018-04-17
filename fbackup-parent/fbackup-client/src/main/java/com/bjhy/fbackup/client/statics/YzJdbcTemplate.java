package com.bjhy.fbackup.client.statics;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.bjhy.fbackup.client.domain.DirectoryTypePicture;

/**
 * 狱政的JdbcTemplate
 * @author wubo
 */
public class YzJdbcTemplate extends JdbcTemplate{
	private DirectoryTypePicture directoryTypePicture;
	private DataSource driverManagerDataSource;
	public DirectoryTypePicture getDirectoryTypePicture() {
		return directoryTypePicture;
	}
	public void setDirectoryTypePicture(DirectoryTypePicture directoryTypePicture) {
		this.directoryTypePicture = directoryTypePicture;
	}
	public DataSource getDriverManagerDataSource() {
		return driverManagerDataSource;
	}
	public void setDriverManagerDataSource(DataSource driverManagerDataSource) {
		this.driverManagerDataSource = driverManagerDataSource;
	}
}
