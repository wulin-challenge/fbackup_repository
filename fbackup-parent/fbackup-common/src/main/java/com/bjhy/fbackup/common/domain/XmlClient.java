package com.bjhy.fbackup.common.domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * xml的客户端配置
 * @author wubo
 *
 */
public class XmlClient {
	
	/**
	 * 图片
	 */
	public static final String DIRECTORY_TYPE_PICTURE = "picture";
	
	/**
	 * 数据库
	 */
	public static final String DIRECTORY_TYPE_DATABASE = "database";
	
	/**
	 * zookeeper地址
	 */
	private String zookeeperAddress;
	
	/**
	 * zookeeper的超时时间
	 */
	private long zookeeperTimeout=5555;
	
	/**
	 * 客户端编号
	 */
	private String clientNumber;
	
	/**
	 * 客户端名称
	 */
	private String clientName;
	
	/**
	 * 读取目录(可以为多个)
	 */
	private Map<String,List<String>> readDirectory = new LinkedHashMap<String,List<String>>();

	public String getZookeeperAddress() {
		return zookeeperAddress;
	}

	public void setZookeeperAddress(String zookeeperAddress) {
		this.zookeeperAddress = zookeeperAddress;
	}

	public Map<String, List<String>> getReadDirectory() {
		return readDirectory;
	}

	public void setReadDirectory(Map<String, List<String>> readDirectory) {
		this.readDirectory = readDirectory;
	}

	public long getZookeeperTimeout() {
		return zookeeperTimeout;
	}

	public void setZookeeperTimeout(long zookeeperTimeout) {
		this.zookeeperTimeout = zookeeperTimeout;
	}
	
	/**
	 * 得到目录类型
	 * @param directoryType
	 * @return
	 */
	public static String getDirectoryType(String directoryType){
		if(DIRECTORY_TYPE_PICTURE.equalsIgnoreCase(directoryType)){
			return DIRECTORY_TYPE_PICTURE;
		}else if(DIRECTORY_TYPE_DATABASE.equalsIgnoreCase(directoryType)){
			return DIRECTORY_TYPE_DATABASE;
		}
		return "";
	}

	public String getClientNumber() {
		return clientNumber;
	}

	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
}
