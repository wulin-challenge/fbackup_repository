package com.bjhy.fbackup.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.druid.pool.DruidDataSource;
import com.bjhy.fbackup.common.annotation.Column;
import com.bjhy.fbackup.common.annotation.EntityTable;
import com.bjhy.fbackup.common.annotation.Id;
import com.bjhy.fbackup.common.extension.ExtensionLoader;

/**
 *存储sql工具类
 * @author wubo
 */
public class RepositorySqlUtil {
	
	/**
	 * 得到insql语句
	 * @param entity
	 * @return
	 */
	public static <T> String getInsertSql(T entity,Map<String, Object> data){
		StringBuffer inserSql = new StringBuffer("insert into "+getTableName(entity)+"(");
		StringBuffer valuesSql = new StringBuffer(" values(");
		
		Set<Entry<String, Object>> entrySet = data.entrySet();
		
		int i=0;
		for (Entry<String, Object> entry : entrySet) {
			if(i==0){
				inserSql.append(entry.getKey());
				valuesSql.append(":"+entry.getKey());
			}else{
				inserSql.append(","+entry.getKey());
				valuesSql.append(",:"+entry.getKey());
			}
			i++;
		}
		
		inserSql.append(")");
		valuesSql.append(")");
		return inserSql.toString()+valuesSql.toString();
	}
	
	/**
	 * 得到updateSql
	 * @param entity
	 * @param data
	 * @return
	 */
	public static <P,T> String getUpdateSql(T entity,Map<String, Object> data){
		StringBuffer updateSql = new StringBuffer("update "+getTableName(entity)+" set ");
		StringBuffer whereSql = new StringBuffer(" where 1=1 and "+getIdLabel(entity.getClass())+"=:"+getIdLabel(entity.getClass()));
		
		Set<Entry<String, Object>> entrySet = data.entrySet();
		int i=0;
		for (Entry<String, Object> entry : entrySet) {
			if(i==0){
				updateSql.append(entry.getKey()+"=:"+entry.getKey());
			}else{
				updateSql.append(","+entry.getKey()+"=:"+entry.getKey());
			}
			i++;
		}
		
		return updateSql.toString()+whereSql.toString();
	}
	
	/**
	 * 得到deleteSql
	 * @param entityClass 实体的class
	 * @param tableName 表名
	 * @return
	 */
	public static <T> String getDeleteSql(Class<T> entityClass,String tableName){
		String idLabel = getIdLabel(entityClass);
		return "delete from "+tableName+" where 1=1 and "+idLabel+"=:"+idLabel;
	}
	
	/**
	 * 得到表名
	 * @param entity
	 * @return
	 */
	public static <T> String getTableName(T entity){
		String tableName = entity.getClass().getSimpleName();
		EntityTable entityTable = entity.getClass().getAnnotation(EntityTable.class);
		
		if(entityTable != null){
			tableName = entityTable.name();
		}
		return tableName;
	}
	
	/**
	 * 将实体转换为map
	 * @param entity
	 * @return
	 */
	public static <T> Map<String,Object> convertEntityToMap(Class<T> entityClass){
		try {
			T entity = entityClass.newInstance();
			return convertEntityToMap(entity);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 将条件转换为where后的sql
	 * @param conditions
	 * @return
	 */
	public static String conditionToWhereSql(Map<String,Object> conditions){
		StringBuffer whereSql = new StringBuffer();
		
		Set<Entry<String, Object>> entrySet = conditions.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			whereSql.append(" and "+entry.getKey()+"=:"+entry.getKey());
		}
		return whereSql.toString();
	}
	
	/**
	 * 将实体转换为map
	 * @param entity
	 * @return
	 */
	public static <T> Map<String,Object> convertEntityToMap(T entity){
		Map<String,Object> data = new LinkedHashMap<String,Object>();
		Field[] declaredFields = entity.getClass().getDeclaredFields();
		for (Field field : declaredFields) {
			String name = field.getName();
			
			String methodName= name.substring(0,1).toUpperCase()+name.substring(1);
			try {
				Method method = entity.getClass().getMethod("get"+methodName);
				Object value = method.invoke(entity);
				data.put(getColumnName(field), value);
			} catch (Exception e) {
				LoggerUtils.error(name+"转换为map时失败!!",e);
			}
		}
		return data;
	}
	
	/**
	 * 得到列名称
	 * @param field
	 * @return
	 */
	private static String getColumnName(Field field){
		String name = field.getName();
		Column annotation = field.getAnnotation(Column.class);
		if(annotation != null){
			name = annotation.name();
		}
		return name;
	}
	
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
	 * 得到Id的属性名称
	 * @param entityClass
	 * @return
	 */
	public static String getIdLabel(Class<?> entityClass){
		Field[] fields = entityClass.getDeclaredFields();
		for (Field field : fields) {
			Id id = field.getAnnotation(Id.class);
			
			if(id != null){
				return field.getName();
			}
		}
		return null;
	}
	
	/**
	 * 得到Id的值
	 * @param entity
	 * @return
	 */
	public static <T> Object getIdValue(T entity){
		Field[] declaredFields = entity.getClass().getDeclaredFields();
		for (Field field : declaredFields) {
			Id annotation = field.getAnnotation(Id.class);
			if(annotation != null){
				String name = field.getName();
				
				String methodName= name.substring(0,1).toUpperCase()+name.substring(1);
				try {
					Method method = entity.getClass().getMethod("get"+methodName);
					Object value = method.invoke(entity);
					return value;
				} catch (Exception e) {
					LoggerUtils.error(name+"转换为Entity时失败!!",e);
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 得到select查询语句
	 * @param columns
	 * @param tableName
	 * @return
	 */
	public static String getSelectSql(List<String> columns,String tableName){
		StringBuffer selectSql = new StringBuffer("select ");
		int i=0;
		for (String column : columns) {
			if(i == 0){
				selectSql.append(column);
			}else{
				selectSql.append(","+column);
			}
			i++;
		}
		selectSql.append(" from "+tableName+" where 1=1 ");
		return selectSql.toString();
	}
	
	/**
	 * 将listMap转为ListEntity
	 * @param entityClass
	 * @param ListData
	 * @return
	 */
	public static <T> List<T> listMaptoListEntity(Class<T> entityClass,List<Map<String,Object>> ListData){
		List<T> entityList = new ArrayList<T>();
		 
		for (Map<String, Object> data : ListData) {
			entityList.add(mapToEntity(entityClass, data));
		}
		return entityList;
	}
	
	/**
	 * 将map转换为实体
	 * @param entityClass
	 * @param data
	 * @return
	 */
	public static <T> T mapToEntity(Class<T> entityClass,Map<String,Object> data){
		T entity = null;
		try {
			entity = entityClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e1) {
			LoggerUtils.error("创建对象实例失败",e1);
		}
		Field[] declaredFields = entityClass.getDeclaredFields();
		
		for (Field field : declaredFields) {
			String name = field.getName();
			
			String methodName= name.substring(0,1).toUpperCase()+name.substring(1);
			try {
				Method method = entity.getClass().getMethod("set"+methodName,field.getType());
				Object value = data.get(getColumnName(field));
				Class<?> targetValueType = field.getType();
				//将日期转为字符串格式
				if(value instanceof Date && targetValueType == String.class){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					value = sdf.format(value);
				}
				method.invoke(entity,value);
			} catch (Exception e) {
				LoggerUtils.error(name+"转换为Entity时失败!!",e);
			}
		}
		return entity;
	}
	
}
