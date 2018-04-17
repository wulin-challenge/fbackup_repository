package com.bjhy.fbackup.common.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * xml的服务端配置
 * @author wubo
 *
 */
public class XmlServer {
	
	/**
	 * 静态资源的
	 */
	public static final String STORE_TYPE_STATIC = "static";
	
	/**
	 * 文件存储的
	 */
	public static final String STORE_TYPE_FILE = "file";
	
	/**
	 * zookeeper地址
	 */
	private String zookeeperAddress;
	
	/**
	 * zookeeper的超时时间
	 */
	private long zookeeperTimeout=5555;
	
	/**
	 * 服务端编号
	 */
	private String serverNumber;
	
	/**
	 * 服务端名称
	 */
	private String serverName;
	
	/**
	 * 存储目录目录(可以为多个)
	 */
	private Map<String,List<String>> storeDirectory = new LinkedHashMap<String,List<String>>();


	public String getZookeeperAddress() {
		return zookeeperAddress;
	}

	public void setZookeeperAddress(String zookeeperAddress) {
		this.zookeeperAddress = zookeeperAddress;
	}

	public long getZookeeperTimeout() {
		return zookeeperTimeout;
	}

	public void setZookeeperTimeout(long zookeeperTimeout) {
		this.zookeeperTimeout = zookeeperTimeout;
	}

	public Map<String, List<String>> getStoreDirectory() {
		return storeDirectory;
	}

	public void setStoreDirectory(Map<String, List<String>> storeDirectory) {
		this.storeDirectory = storeDirectory;
	}
	
	public String getServerNumber() {
		return serverNumber;
	}

	public void setServerNumber(String serverNumber) {
		this.serverNumber = serverNumber;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	/**
	 * 得到存储类型
	 * @param directoryType
	 * @return
	 */
	public static String getStoreType(String storeType){
		if(STORE_TYPE_STATIC.equalsIgnoreCase(storeType)){
			return STORE_TYPE_STATIC;
		}else if(STORE_TYPE_FILE.equalsIgnoreCase(storeType)){
			return STORE_TYPE_FILE;
		}
		return "";
	}

}
