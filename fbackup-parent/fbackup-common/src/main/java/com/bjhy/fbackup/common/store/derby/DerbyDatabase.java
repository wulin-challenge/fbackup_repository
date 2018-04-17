package com.bjhy.fbackup.common.store.derby;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.alibaba.druid.pool.DruidDataSource;
import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.common.util.FileUtil;
import com.bjhy.fbackup.common.util.LoggerUtils;

/**
 * derby数据库工具类
 * @author wubo
 */
public class DerbyDatabase {
	
	// 数据库驱动
	private static final String DERBY_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
	//数据库的url
	private static String DERBY_URL = "jdbc:derby:";// 数据库URL前缀
	//数据库的更文件夹
	private static final String DERBY_ROOT_DB = System.getProperty("user.dir")+"/db/";
	
	/**
	 * 初始化derby
	 */
	public static void initDerby(String databaseName){
		createDataBase(databaseName);//创建数据库
		getNamedParameterJdbcTemplate();//得到 NamedParameterJdbcTemplate
	}
	
	/**
	 * 创建数据库
	 * @param databaseName 数据库名称
	 */
	private static void createDataBase(String databaseName){
		//加载derby数据库驱动
		loadDerbyDatabaseDriver();
		
		String derbyDatabasePath = FileUtil.replaceSpritAndEnd(DERBY_ROOT_DB)+databaseName;
		
		DERBY_URL += derbyDatabasePath;//得到真正的derby_url
		Boolean derbyDatabaseExist = derbyDatabaseExist(derbyDatabasePath);
		if(!derbyDatabaseExist){
			try {
				DriverManager.getConnection(DERBY_URL + ";create=true");//真正的创建数据库
			} catch (SQLException e) {
				LoggerUtils.error("数据库创建失败!", e);
			} 
		}
	}
	
	/**
	 * 加载derby数据库驱动
	 */
	private static void loadDerbyDatabaseDriver(){
		try {
			Class.forName(DERBY_DRIVER);
		} catch (ClassNotFoundException e) {
			LoggerUtils.error("没有找到 org.apache.derby.jdbc.EmbeddedDriver 这个驱动程序", e);
		}
	}
	
	/**
	 * derby数据库存在吗?
	 * @param derbyDatabasePath derby数据库的路径
	 * @return 存在返回true,否则返回false
	 */
	private static Boolean derbyDatabaseExist(String derbyDatabasePath){
		File derbyDatabase = new File(derbyDatabasePath);
		if(derbyDatabase.exists()){
			return true;
		}
		return false;
	}
	
	/**
	 * 得到设置数据源,这使用的是阿里巴巴的DruidDataSource,放弃使用spring的DriverManagerDataSource和c3p0
	 * @return
	 */
	private static DataSource getDataSource(){
		DruidDataSource dataSource = new DruidDataSource();
		try {
			dataSource.setUrl(DERBY_URL);
			dataSource.setDriverClassName(DERBY_DRIVER);
			
			//设置这个Name是为了防止 DruidDataSource 抛出 
			dataSource.setName(FileUtil.getUUID());
			dataSource.setMinIdle(0);
			dataSource.setMaxActive(200);
		} catch (Exception e) {
			LoggerUtils.error("创建数据源失败!!",e);
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
	private static void getNamedParameterJdbcTemplate(){
		JdbcTemplate jdbcTemplate = getJdbcTemplate();//getJdbcTemplate
		NamedParameterJdbcTemplate namedParameterJdbcTemplate  = new NamedParameterJdbcTemplate(jdbcTemplate);
		ExtensionLoader.setInstance(NamedParameterJdbcTemplate.class, namedParameterJdbcTemplate);
	}
	
//	public static void main(String[] args) {
//		System.out.println();
//		DerbyDatabase.createDataBase("derby");
//		DataSource dataSource = DerbyDatabase.getDataSource();
//		System.out.println(dataSource);
//	}
}
