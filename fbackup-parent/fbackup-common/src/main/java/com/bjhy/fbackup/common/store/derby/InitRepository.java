package com.bjhy.fbackup.common.store.derby;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import com.bjhy.fbackup.common.annotation.Column;
import com.bjhy.fbackup.common.annotation.EntityTable;
import com.bjhy.fbackup.common.annotation.Id;
import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.common.reflections.ScanningPackage;
import com.bjhy.fbackup.common.util.ConstantUtil;
import com.bjhy.fbackup.common.util.LoggerUtils;

/**
 * 初始化repository
 * @author wubo
 *
 */
public class InitRepository {
	
	/**
	 * 创建表的语句
	 */
	private StringBuffer createTableSql = new StringBuffer("create table ");
	
	/**
	 * 创建的表名
	 */
	private String createTableName = "";
	
	private InitRepository(){}//私有
	
	public static void loadEntityTable(){
		loadEntityTable(ConstantUtil.SCANNING_PACKAGE);
	}
	
	/**
	 * 加载entityTable
	 * @param scanningPackage
	 */
	private static void loadEntityTable(String scanningPackage){
		List<Class<?>> classList = ScanningPackage.findClassByPackageAndAnnotation(scanningPackage, EntityTable.class);
		for (Class<?> clazz : classList) {
			try {
				InitRepository initRepository = new InitRepository();
				initRepository.dealWithEntityTable(clazz); //处理EntityTable
				
				initRepository.dealWithIdAndColumn(clazz); //处理Id和column两个注解
				initRepository.dealWithSuffix();//处理后缀
				initRepository.executeCreateTable();//执行创建表语句
			} catch (Exception e) {
				LoggerUtils.error("生成表语句失败",e);
			}
		}
	}
	
	/**
	 * 处理后缀
	 */
	private void dealWithSuffix(){
		createTableSql.append(")");
	}
	
	/**
	 * 执行创建表语句
	 */
	private void executeCreateTable(){
		JdbcTemplate jdbcTemplate = ExtensionLoader.getInstance(JdbcTemplate.class);
		if(jdbcTemplate != null){
			Statement stmt = null;
			Connection conn = null;
			try {
				conn = jdbcTemplate.getDataSource().getConnection();
				stmt = conn.createStatement();
				stmt.executeUpdate(createTableSql.toString());
			} catch (SQLException e) {
				LoggerUtils.info(createTableName+" 表已经存在!"+e.getMessage());
			}finally{
				try {
					stmt.close();
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * 处理EntityTable 得到表名
	 * @param entityTableClass
	 */
	private void dealWithEntityTable(Class<?> entityTableClass){
		EntityTable entityTable = entityTableClass.getAnnotation(EntityTable.class);
		String name = entityTable.name();
		if(StringUtils.isNotBlank(name)){
			createTableSql.append(name+"(");
			createTableName = name;
		}else{
			String simpleName = entityTableClass.getSimpleName();
			createTableSql.append(simpleName+"(");
			createTableName = simpleName;
		}
	}
	
	/**
	 * 处理Id和column两个注解
	 * @param entityTableClass
	 */
	private void dealWithIdAndColumn(Class<?> entityTableClass){
		Field[] columns = entityTableClass.getDeclaredFields();
		int i = 0;
		for (Field field : columns) {
			dealWithIdAndColumn(field, i);
			i++;
		}
	}
	
	/**
	 * 处理主键和普通列
	 * @param field
	 * @param i 第几个属性
	 */
	private void dealWithIdAndColumn(Field field,int i){
		Id id = field.getAnnotation(Id.class);
		if(id != null){
//			field.
			dealWithColumn(field, i);
			createTableSql.append(" primary key");
		}else{
			dealWithColumn(field, i);
		}
		
	}
	
	/**
	 * 处理普通列
	 * @param field
	 * @param i
	 */
	private void dealWithColumn(Field field,int i){
		Column column = field.getAnnotation(Column.class);
		if(column != null){
			//列的名称
			String name = getColumnName(column, field);
			
			if(i == 0){
				createTableSql.append(name+" ");
			}else{
				createTableSql.append(","+name+" ");
			}
			
			//列的类型
			String dataType = column.dataType();
			if(StringUtils.isBlank(dataType)){
				dataType = getMetaDataType(field);
			}
			
			//如果是字符型就有长度
			if(field.getType() == String.class){
				dataType+="("+column.length()+")";
			}
			createTableSql.append(dataType+" ");
			
		}else{
			String name = field.getName();
			
			if(i == 0){
				createTableSql.append(name+" ");
			}else{
				createTableSql.append(","+name+" ");
			}
			
			String dataType = getMetaDataType(field);
			//如果是字符型就有长度
			if(field.getType() == String.class){
				dataType+="(255)";
			}
			createTableSql.append(dataType+" ");
		}
	}
	
	/**
	 * 得到列的名称
	 * @param column
	 * @param field
	 * @return
	 */
	private String getColumnName(Column column,Field field){
		String name = column.name();
		if(StringUtils.isBlank(name)){
			name = field.getName();
		}
		return name;
	}
	
	/**
	 * 得到元素数据类型
	 * @param field
	 * @return
	 */
	private String getMetaDataType(Field field){
		Class<?> type = field.getType();
		if(type == String.class){
			return "varchar";
		}else if(type == Date.class){
			return "date";
		}
		
		return "";
	}
	
//	public static void main(String[] args) {
//		DerbyDatabase.initDerby("test_derby");
//		loadEntityTable();
//	}

}
