package com.bjhy.fbackup.client.core.test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.DataSource;

import junit.framework.Test;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.alibaba.druid.pool.DruidDataSource;
import com.bjhy.fbackup.common.annotation.Column;
import com.bjhy.fbackup.common.annotation.EntityTable;
import com.bjhy.fbackup.common.annotation.Id;
import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.common.util.ConstantUtil;

/**
 *测试数据源连接
 * @author wubo
 */
public class TestDatasourceConnectNumber {
	

	
	/**
	 * 得到所用的列名
	 * @param jdbcTemplate
	 * @param tableName
	 * @return
	 */
	public static List<String> getAllColumn(JdbcTemplate jdbcTemplate,String tableName){
		String selectSql = "select * from "+tableName +" where 1=2";
		List<String> columns = new ArrayList<>();
		
		try (
				Connection conn = jdbcTemplate.getDataSource().getConnection();
				PreparedStatement pst = conn.prepareStatement(selectSql);
				ResultSet rs = pst.executeQuery();){
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				columns.add(rsmd.getColumnLabel(i).toUpperCase());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return columns;
	}
	
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		
		final TestDatasourceConnectNumber repositorySqlUtil = new TestDatasourceConnectNumber();
		final JdbcTemplate jdbcTemplate = repositorySqlUtil.getJdbcTemplate();
		final NamedParameterJdbcTemplate namedbcTemplate = repositorySqlUtil.getNamedParameterJdbcTemplate();
		for (int i = 0; i < 500; i++) {
			final int j = i;
			
			Thread thread = new Thread(){
				@Override
				public void run() {
					repositorySqlUtil.test(j,jdbcTemplate,namedbcTemplate);
					super.run();
				}
				
			};
			thread.start();
		}
	}
	
	private void test(int j,JdbcTemplate jdbcTemplate,NamedParameterJdbcTemplate namedbcTemplate){
		List<String> allColumn = getAllColumn(jdbcTemplate, "base_file_transfer");
		System.out.println(j+"---"+allColumn);
		
//		
//		String totalDataSql = "select count(1) from base_file_transfer where fileTransferStatus=:fileTransferStatus";
//		
//		Map<String,Object> totalDataParams = new HashMap<String,Object>();
//		totalDataParams.put("fileTransferStatus", ConstantUtil.FILE_TRANSFER_STATUS_NO_TRANSFER);
////			Integer  dataTotal = fileTransferEntityDao.findDecimalNumberBySql(totalDataSql, totalDataParams);
//	
//			int queryForObject = namedbcTemplate.queryForObject(totalDataSql, totalDataParams, int.class);
//		
//		System.out.println(j+"---"+queryForObject);
	}
	
	
	
	/**
	 * 得到设置数据源,这使用的是阿里巴巴的DruidDataSource,放弃使用spring的DriverManagerDataSource和c3p0
	 * @return
	 */
	private static DataSource getDataSource(){
		DruidDataSource dataSource = new DruidDataSource();
		try {
			
//			"staticRootPath":"E:/CAS_Platform/nginx-1.9.5/statics",
//			"staticServiceUrl":"http://192.168.0.251:9898",
//			"staticType":"static",
//			"databaseUsername":"yzpt",
//			"databasePassword":"123456789",
//			"databaseUrl":"jdbc:oracle:thin:@192.168.0.222:1521:orcl",
//			"databaseDriver":"oracle.jdbc.driver.OracleDriver"
//			
			
			dataSource.setUrl("jdbc:oracle:thin:@192.168.0.222:1521:orcl");
			dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
			dataSource.setUsername("yzpt");
			dataSource.setPassword("123456789");
//			dataSource.setUrl(DERBY_URL);
//			dataSource.setDriverClassName(DERBY_DRIVER);
			
			//设置这个Name是为了防止 DruidDataSource 抛出 
			dataSource.setName("ddd222ddd");
			dataSource.setMinIdle(0);
			dataSource.setMaxActive(200);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ExtensionLoader.setInstance(DataSource.class, dataSource);
		return dataSource;
	}
	
	/**
	 * 得到jdbcTemplate
	 * @return
	 */
	private static JdbcTemplate getJdbcTemplate(){
		JdbcTemplate jdbcTemplate = new JdbcTemplate();
		jdbcTemplate.setDataSource(getDataSource());
		
		ExtensionLoader.setInstance(JdbcTemplate.class, jdbcTemplate);
		return jdbcTemplate;
	}
	
	/**
	 * 得到 NamedParameterJdbcTemplate
	 * 
	 * SqlParameterSource ps=new BeanPropertySqlParameterSource(versionCheckEntity);//从versionCheckEntity中取出数据，与sql语句中一一对应将数据换进去  
		namedNativeTemplate.update(sql, ps);
	 */
	private static NamedParameterJdbcTemplate getNamedParameterJdbcTemplate(){
		JdbcTemplate jdbcTemplate = getJdbcTemplate();//getJdbcTemplate
		NamedParameterJdbcTemplate namedParameterJdbcTemplate  = new NamedParameterJdbcTemplate(jdbcTemplate);
		return namedParameterJdbcTemplate;
	}
	
}
