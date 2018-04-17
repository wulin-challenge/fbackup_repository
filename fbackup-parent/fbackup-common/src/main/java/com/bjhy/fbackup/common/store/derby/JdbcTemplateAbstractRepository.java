package com.bjhy.fbackup.common.store.derby;

import java.lang.reflect.ParameterizedType;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.bjhy.fbackup.common.annotation.EntityTable;
import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.common.util.LoggerUtils;
import com.bjhy.fbackup.common.util.RepositorySqlUtil;

/**
 * 这是采用jdbcTemplate封装的一系列基本通用方法,全部采用类似于hibernate的操作,这是实现部分
 * @author wubo
 * @param <P> 实体的主键类型
 * @param <T> 实体的类型
 */
@SuppressWarnings({"unchecked"})
public abstract class JdbcTemplateAbstractRepository<P,T> implements JdbcTemplateRepository<P,T>{
	
	private Class<T> entityClass; //这是实体的class
	private String tableName; //这是实体的对应的表名
	
	private NamedParameterJdbcTemplate jdbcTemplate = ExtensionLoader.getInstance(NamedParameterJdbcTemplate.class);
	private JdbcTemplate jdbcTemplate2 = ExtensionLoader.getInstance(JdbcTemplate.class);
	
	public JdbcTemplateAbstractRepository(){
		//通过反射得到T的真实类型
		ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
		this.entityClass = (Class<T>) pt.getActualTypeArguments()[1];
		
		String tName = entityClass.getSimpleName();
		EntityTable annotation = entityClass.getAnnotation(EntityTable.class);
		
		if(annotation != null){
			tName = annotation.name();
		}
		
		this.tableName = tName;
		System.out.println("clazz = " + entityClass.getName());
	}
	
	@Override
	public void saveEntity(T entity) throws SQLException {
		Map<String, Object> params = RepositorySqlUtil.convertEntityToMap(entity);
		String insertSql = RepositorySqlUtil.getInsertSql(entity, params);
		jdbcTemplate.update(insertSql, params);
	}

	@Override
	public void updateEntity(T entity) throws SQLException {
		P id = (P) RepositorySqlUtil.getIdValue(entity);
		T entity2 = findOneById(id);
		
		if(entity2 != null ){
			Map<String, Object> params = RepositorySqlUtil.convertEntityToMap(entity);
			String updateSql = RepositorySqlUtil.getUpdateSql(entity, params);
			jdbcTemplate.update(updateSql, params);
		}else{
			throw new SQLException("当前更新数据不存在");
		}
	}

	@Override
	public void saveOrUpdateEntity(T entity) throws SQLException {
		P id = (P) RepositorySqlUtil.getIdValue(entity);
		T entity2 = findOneById(id);
		
		if(entity2 != null ){
			updateEntity(entity);
		}else{
			saveEntity(entity);
		}
	}

	@Override
	public void deleteEntity(T entity) throws SQLException {
		P id = (P) RepositorySqlUtil.getIdValue(entity);
		deleteEntityById(id);
	}

	@Override
	public void deleteEntityById(P id) throws SQLException {
		Map<String,Object> params = new LinkedHashMap<String,Object>();
		params.put(RepositorySqlUtil.getIdLabel(entityClass), id);
		
		String deleteSql = RepositorySqlUtil.getDeleteSql(entityClass, tableName);
		jdbcTemplate.update(deleteSql, params);
	}

	@Override
	public List<T> findListByCondition(Map<String, Object> conditions)throws SQLException {
		return findListByCondition(conditions, null, null);
	}
	
	@Override
	public List<T> findListByCondition(String sql,Map<String, Object> conditions) throws SQLException {
		List<Map<String, Object>> dataList = jdbcTemplate.queryForList(sql,conditions);
		List<T> listMaptoListEntity = RepositorySqlUtil.listMaptoListEntity(entityClass, dataList);
		return listMaptoListEntity;
	}

	@Override
	public List<T> findListByCondition(Map<String, Object> conditions,String orderByName, String orderByType) throws SQLException {
		List<String> columns = RepositorySqlUtil.getAllColumn(jdbcTemplate2, tableName);
		String selectSql = RepositorySqlUtil.getSelectSql(columns, tableName);
		
		selectSql += RepositorySqlUtil.conditionToWhereSql(conditions);
		
		if(StringUtils.isNotBlank(orderByName) && StringUtils.isNotBlank(orderByType)){
			selectSql += " order by "+orderByName+" "+orderByType;
		}
		
		List<Map<String, Object>> dataList = jdbcTemplate.queryForList(selectSql,conditions);
		List<T> listMaptoListEntity = RepositorySqlUtil.listMaptoListEntity(entityClass, dataList);
		return listMaptoListEntity;
	}

	
	@Override
	public Integer findDecimalNumber(Map<String, Object> conditions)throws SQLException {
		String decimalSql = "select count(1) from "+tableName+" where 1=1 "+RepositorySqlUtil.conditionToWhereSql(conditions);
//		int queryForInt = jdbcTemplate.queryForInt(decimalSql,conditions);
		
		int queryForObject = jdbcTemplate.queryForObject(decimalSql, conditions, int.class);
		return queryForObject;
	}

	@Override
	public T findOneByCondition(Map<String, Object> conditions)throws SQLException {
		List<T> entityList = findListByCondition(conditions);
		if(entityList.size() >1){
			LoggerUtils.error("当前查询的实体存在多条!!");
		}
		if(entityList.size() >0){
			return entityList.get(0);
		}
		return null;
	}

	@Override
	public T findOneById(P id) throws SQLException {
		List<String> columns = RepositorySqlUtil.getAllColumn(jdbcTemplate2, tableName);
		String selectSql = RepositorySqlUtil.getSelectSql(columns, tableName);
		selectSql += " and "+RepositorySqlUtil.getIdLabel(entityClass)+"=:"+RepositorySqlUtil.getIdLabel(entityClass);
		
		//参数
		Map<String,Object> params = new LinkedHashMap<String,Object>();
		params.put(RepositorySqlUtil.getIdLabel(entityClass), id);
		
		Map<String, Object> data;
		try {
			data = jdbcTemplate.queryForMap(selectSql, params);
		} catch (DataAccessException e) {
			LoggerUtils.warn("当前查询的数据不存在,查询sql:"+selectSql);
//			LoggerUtils.warn("当前数据不存在",e);
			return null;
		}
		T mapToEntity = RepositorySqlUtil.mapToEntity(entityClass, data);
		return mapToEntity;
	}

	@Override
	public List<T> findListByIds(List<P> ids) {
		String idLabel = RepositorySqlUtil.getIdLabel(entityClass);
		List<String> columns = RepositorySqlUtil.getAllColumn(jdbcTemplate2, tableName);
		String selectSql = RepositorySqlUtil.getSelectSql(columns, tableName);
		selectSql += " and "+idLabel+" in(:ids)";
		
		Map<String,Object> params = new LinkedHashMap<String,Object>();
		params.put("ids", ids);
		
		List<Map<String, Object>> dataList = jdbcTemplate.queryForList(selectSql, params);
		
		List<T> listMaptoListEntity = RepositorySqlUtil.listMaptoListEntity(entityClass, dataList);
		return listMaptoListEntity;
	}
	
	@Override
	public List<Map<String, Object>> findAll2() throws SQLException {
		List<String> columns = RepositorySqlUtil.getAllColumn(jdbcTemplate2, tableName);
		String selectSql = RepositorySqlUtil.getSelectSql(columns, tableName);
		
		List<Map<String, Object>> dataList = jdbcTemplate.queryForList(selectSql,Collections.EMPTY_MAP);
		return dataList;
	}
	
	@Override
	public List<T> findAll() throws SQLException {
		List<String> columns = RepositorySqlUtil.getAllColumn(jdbcTemplate2, tableName);
		String selectSql = RepositorySqlUtil.getSelectSql(columns, tableName);
		
		List<Map<String, Object>> dataList = jdbcTemplate.queryForList(selectSql,Collections.EMPTY_MAP);
		List<T> listMaptoListEntity = RepositorySqlUtil.listMaptoListEntity(entityClass, dataList);
		return listMaptoListEntity;
	}
	
	@Override
	public void executeSql(String sql, Map<String, Object> params) {
		jdbcTemplate.update(sql, params);
	}

	@Override
	public void executeSql(String sql) {
		executeSql(sql,Collections.EMPTY_MAP);
	}
	
	@Override
	public Integer findDecimalNumberBySql(String sql,Map<String, Object> conditions) throws SQLException {
		int queryForObject = jdbcTemplate.queryForObject(sql, conditions, int.class);
		return queryForObject;
	}

	@Override
	public Integer findDecimalNumberBySql(String sql) throws SQLException {
		return findDecimalNumberBySql(sql,Collections.EMPTY_MAP);
	}

	@Override
	public T findOneEntityBySql(String sql, Map<String, Object> conditions)throws SQLException {
		
		Map<String, Object> data;
		try {
			data = jdbcTemplate.queryForMap(sql, conditions);
		} catch (DataAccessException e) {
			LoggerUtils.warn("当前数据不存在",e);
			return null;
		}
		T mapToEntity = RepositorySqlUtil.mapToEntity(entityClass, data);
		return mapToEntity;
	}

	@Override
	public T findOneEntityBySql(String sql) throws SQLException {
		return findOneEntityBySql(sql, Collections.EMPTY_MAP);
	}

	@Override
	public Map<String, Object> findOneMapBySql(String sql,Map<String, Object> conditions) throws SQLException {
		
		Map<String, Object> data;
		try {
			data = jdbcTemplate.queryForMap(sql, conditions);
		} catch (DataAccessException e) {
			LoggerUtils.warn("当前数据不存在",e);
			return null;
		}
		return data;
	}

	@Override
	public Map<String, Object> findOneMapBySql(String sql) throws SQLException {
		return findOneMapBySql(sql,Collections.EMPTY_MAP);
	}

	@Override
	public List<T> findEntityListBySql(String sql, Map<String, Object> params) {
		List<Map<String, Object>> dataList = jdbcTemplate.queryForList(sql,params);
		List<T> listMaptoListEntity = RepositorySqlUtil.listMaptoListEntity(entityClass, dataList);
		return listMaptoListEntity;
	}

	@Override
	public List<T> findEntityListBySql(String sql) {
		return findEntityListBySql(sql,Collections.EMPTY_MAP);
	}

	@Override
	public List<Map<String, Object>> findMapListBySql(String sql,Map<String, Object> params) {
		return jdbcTemplate.queryForList(sql,params);
	}

	@Override
	public List<Map<String, Object>> findMapListBySql(String sql) {
		return findMapListBySql(sql, Collections.EMPTY_MAP);
	}
}
