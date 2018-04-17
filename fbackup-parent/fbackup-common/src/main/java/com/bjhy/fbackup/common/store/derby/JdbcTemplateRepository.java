package com.bjhy.fbackup.common.store.derby;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 这是采用jdbcTemplate封装的一系列基本通用方法,全部采用类似于hibernate的操作
 * <P,T> p:表示的主键的类型,T:表示的是实体的类型
 * @author wubo
 *
 */
public interface JdbcTemplateRepository<P,T> {
	
	/**
	 * 保存实体,若出错就抛出 SQLException异常
	 * @param entity 实体
	 * @throws SQLException
	 */
	void saveEntity(T entity) throws SQLException;
	
	/**
	 * 更新实体,若出错就抛出 SQLException异常
	 * @param entity 实体
	 * @throws SQLException
	 */
	void updateEntity(T entity) throws SQLException;
	
	/**
	 * 这是用于这些insert,update,delete的sql语句
	 * @param sql
	 * @param params
	 */
	void executeSql(String sql,Map<String,Object> params);
	
	/**
	 * 这是用于这些insert,update,delete的sql语句
	 * @param sql
	 */
	void executeSql(String sql);
	
	/**
	 * 保存或更新实体,若实体数据存在就更新,不存在就保存,若出错就抛出 SQLException异常
	 * @param entity 实体
	 * @throws SQLException
	 */
	void saveOrUpdateEntity(T entity) throws SQLException;
	
	/**
	 * 删除实体
	 * @param entity
	 * @throws SQLException
	 */
	void deleteEntity(T entity) throws SQLException;
	
	/**
	 * 通过id删除实体数据
	 * @param entity
	 * @throws SQLException
	 */
	void deleteEntityById(P id) throws SQLException;
	
	/**
	 * 查找所有的数据实体
	 * @return
	 * @throws SQLException
	 */
	List<T> findAll() throws SQLException;
	/**
	 * 查找所有的数据map
	 * @return
	 * @throws SQLException
	 */
	List<Map<String,Object>> findAll2() throws SQLException;
	
	/**
	 * 通过实体查找Entity的List数据
	 * @param conditions 标记集合 不同条件直接默认采用and进行连接
	 * @return
	 * @throws SQLException
	 */
	List<T> findListByCondition(Map<String,Object> conditions) throws SQLException;
	
	/**
	 * 通过实体查找Entity的List数据
	 * @param sql 自定义查询sql
	 * @param conditions 标记集合 不同条件直接默认采用and进行连接
	 * @return
	 * @throws SQLException
	 */
	List<T> findListByCondition(String sql,Map<String,Object> conditions) throws SQLException;
	
	
	/**
	 * 通过实体查找Entity的List数据
	 * @param conditions 标记集合
	 * @param orderByName 排序字段名
	 * @param orderByType 排序类型
	 * @return
	 * @throws SQLException
	 */
	List<T> findListByCondition(Map<String,Object> conditions,String orderByName,String orderByType) throws SQLException;
	
	/**
	 * 得到符合条件数据行数
	 * @param conditions 条件
	 * @return 返回符合条件数据行数
	 * @throws SQLException
	 */
	Integer findDecimalNumber(Map<String,Object> conditions) throws SQLException;
	
	/**
	 * 通过Sql得到符合条件数据行数
	 * @param conditions 条件
	 * @return 返回符合条件数据行数
	 * @throws SQLException
	 */
	Integer findDecimalNumberBySql(String sql,Map<String,Object> conditions) throws SQLException;
	
	/**
	 * 通过Sql得到符合条件数据行数
	 * @return 返回符合条件数据行数
	 * @throws SQLException
	 */
	Integer findDecimalNumberBySql(String sql) throws SQLException;
	
	/**
	 * 通过条件得到唯一的一条数据
	 * @param conditions 条件集合
	 * @return 返回唯一的一条数据实体
	 */
	T findOneByCondition(Map<String,Object> conditions) throws SQLException;
	
	/**
	 * 通过sql查询并返回唯一的实体
	 * @param sql
	 * @param conditions
	 * @return
	 * @throws SQLException
	 */
	T findOneEntityBySql(String sql,Map<String,Object> conditions) throws SQLException;
	
	/**
	 * 通过sql查询并返回唯一的实体
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	T findOneEntityBySql(String sql) throws SQLException;
	
	/**
	 * 通过sql查询并返回唯一的map数据
	 * @param sql
	 * @param conditions
	 * @return
	 * @throws SQLException
	 */
	Map<String,Object> findOneMapBySql(String sql,Map<String,Object> conditions) throws SQLException;
	
	/**
	 * 通过sql查询并返回唯一的map数据
	 * @param sql
	 * @param conditions
	 * @return
	 * @throws SQLException
	 */
	Map<String,Object> findOneMapBySql(String sql) throws SQLException;
	
	/**
	 * 通过Id得到唯一的体条数据
	 * @param id 条件id
	 * @return 返回唯一的一条数据实体
	 */
	T findOneById(P id) throws SQLException;
	
	/**
	 * 通过Ids得到数据实体集合
	 * @param ids 条件Ids
	 * @return 数据实体集合
	 */
	List<T> findListByIds(List<P> ids);
	
	/**
	 * 通过sql语句进行查询,并返回具体的实体
	 * @param sql
	 * @param params
	 * @return
	 */
	List<T> findEntityListBySql(String sql,Map<String,Object> params);
	
	/**
	 * 通过sql语句进行查询,并返回具体的实体
	 * @param sql
	 * @return
	 */
	List<T> findEntityListBySql(String sql);
	
	/**
	 * 通过sql返回ListMap的数据类型
	 * @param sql
	 * @param params
	 * @return
	 */
	List<Map<String,Object>> findMapListBySql(String sql,Map<String,Object> params);
	
	/**
	 * 通过sql返回ListMap的数据类型
	 * @param sql
	 * @return
	 */
	List<Map<String,Object>> findMapListBySql(String sql);
}
