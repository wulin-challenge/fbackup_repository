package com.bjhy.fbackup.common.util;

/**
 * 常量工具类
 * @author wubo
 */
public class ConstantUtil {
	
	/**
	 * 文件状态(create)
	 */
	public static final String FILE_STATUS_CREATE = "create";
	
	/**
	 * 文件状态(update)
	 */
	public static final String FILE_STATUS_UPDATE = "update";
	
	/**
	 * 文件状态(delete)
	 */
	public static final String FILE_STATUS_DELETE = "delete";
	
	/**
	 * 文件传输状态(alreadyTransfer)
	 */
	public static final String FILE_TRANSFER_STATUS_ALREADY_TRANSFER = "alreadyTransfer";
	
	/**
	 * 文件传输状态(transfering)
	 */
	public static final String FILE_TRANSFER_STATUS_TRANSFERING = "transfering";
	
	/**
	 * 文件传输状态(noTransfer)
	 */
	public static final String FILE_TRANSFER_STATUS_NO_TRANSFER = "noTransfer";
	
	/**
	 * 目录类型(picture)
	 */
	public static final String DIRECTORY_TYPE_PICTURE = "picture";
	
	/**
	 * 目录类型(database)
	 */
	public static final String DIRECTORY_TYPE_DATABASE = "database";
	
	/**
	 * 节点通知状态(client_has_data)
	 */
	public static final String NOTIFY_STATUS_CLIENT_HAS_DATA = "client_has_data";
	
	/**
	 * 节点通知状态(client_no_data)
	 */
	public static final String NOTIFY_STATUS_CLIENT_NO_DATA = "client_no_data";
	
	/**
	 * 节点通知状态(server_has_data)
	 */
	public static final String NOTIFY_STATUS_SERVER_HAS_DATA = "server_has_data";
	
	/**
	 * 节点通知状态(server_no_data)
	 */
	public static final String NOTIFY_STATUS_SERVER_NO_DATA = "server_no_data";
	
	/**
	 * 扫描的根包
	 */
	public static final String SCANNING_PACKAGE = "com.bjhy.fbackup";
	
	/**
	 * derby每页分页的数量
	 */
	public static final int DERBY_PAGE_PER_PAGE_NUMBER = 10;
	
	/**
	 * 静态资源类型
	 */
	public static final String STATIC_TYPE = "static";
	
	/**
	 * 同步所有客户端
	 */
	public static final String SERVER_SYSN_ALL_CLIENT = "ALL";
	
	/**
	 * 传输零字节文件的key
	 */
	public static final String TRANSFER_ZERO_BYTE_FILE_KEY = "transfer_zero_byte_file";
	
}
