package com.bjhy.fbackup.client.base;

import java.util.List;

/**
 * 读取数据库备份文件类型的接口
 * @author wubo
 */
public interface ReadDatabaseType {
	
	/**
	 * 处理数据库备份文件接口
	 * @param directoryType 目录类型
	 * @param databaseList 对应的目录路径
	 */
	public void dealWithDatabase(String directoryType,List<String> databaseList);

}
