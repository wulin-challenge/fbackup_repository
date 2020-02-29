package com.bjhy.fbackup.client.core.statics;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.bjhy.fbackup.client.core.domain.DirectoryTypePicture;
import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.common.util.LoggerUtils;

/**
 * 加载狱政的DataSource
 * @author wubo
 */
public class LoaderYzDatasource {
	
	public YzJdbcTemplate loaderYzJdbcTemplate(DirectoryTypePicture connectConfig){
		YzJdbcTemplate syncTemplate = new YzJdbcTemplate();
		DataSource dataSource = getDataSource(connectConfig);
		DataSource driverManagerDataSource = getDriverManagerDataSource(connectConfig);
		syncTemplate.setDataSource(dataSource);
		syncTemplate.setDriverManagerDataSource(driverManagerDataSource);
		
		Boolean flat = connect(driverManagerDataSource);
		if(flat){
			ExtensionLoader.setInstance(YzJdbcTemplate.class, syncTemplate);
			getNamedParameterJdbcTemplate(syncTemplate);
		}else{
			throw new RuntimeException("当前数据源不能连接");
		}
		return syncTemplate;
	}
	
	/**
	 * 得到 YzNamedParameterJdbcTemplate
	 * 
	 * SqlParameterSource ps=new BeanPropertySqlParameterSource(versionCheckEntity);//从versionCheckEntity中取出数据，与sql语句中一一对应将数据换进去  
		namedNativeTemplate.update(sql, ps);
	 */
	private static void getNamedParameterJdbcTemplate(YzJdbcTemplate jdbcTemplate){
		YzNamedParameterJdbcTemplate namedParameterJdbcTemplate  = new YzNamedParameterJdbcTemplate(jdbcTemplate);
		ExtensionLoader.setInstance(YzNamedParameterJdbcTemplate.class, namedParameterJdbcTemplate);
	}
	
	/**
	 * 测试当前数据源是否可以连接
	 */
	private Boolean connect(DataSource driverManagerDataSource){
		Boolean flat = false;
		Connection connection = null;
		try {
			connection = driverManagerDataSource.getConnection();
			flat = true;
		} catch (SQLException e) {
			e.printStackTrace();
			LoggerUtils.error("当前狱政数据源不能连接");
			flat = false;
		}finally{
			if(connection != null){
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return flat;
	}
	
	/**
	 * 设置数据源,这使用的是阿里巴巴的DruidDataSource,放弃使用spring的DriverManagerDataSource和c3p0
	 * @param connect
	 * @return
	 */
	public DataSource getDataSource(DirectoryTypePicture connect){
		DruidDataSource dataSource = new DruidDataSource();
		try {
			dataSource.setUrl(connect.getDatabaseUrl());
			dataSource.setUsername(connect.getDatabaseUsername());
			dataSource.setPassword(connect.getDatabasePassword());
//			dataSource.setDriverClassName(connect.getConnectDriver());
			
			//设置这个Name是为了防止 DruidDataSource 抛出 
			dataSource.setMinIdle(0);
			dataSource.setMaxActive(200);
			
		} catch (Exception e) {
//			e.printStackTrace();
			LoggerUtils.error("错误信息:"+e.getMessage());
		}
		return dataSource;
	}
	
	/**
	 * 得到DriverManagerDataSource ,用它主要是用来测试是否能够连接成功
	 * @param connect
	 * @return
	 */
	public DataSource getDriverManagerDataSource(DirectoryTypePicture connect){
		DriverManagerDataSource source = new DriverManagerDataSource();
		source.setDriverClassName(connect.getDatabaseDriver());
		source.setUrl(connect.getDatabaseUrl());
		source.setUsername(connect.getDatabaseUsername());
		source.setPassword(connect.getDatabasePassword());
		return source;
	}

}
