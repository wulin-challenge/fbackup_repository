package com.bjhy.fbackup.common.domain;

/**
 * xml配置文件的跟元素
 * @author wubo
 *
 */
public class XmlFbackup {
	
	/**
	 * 服务实例的唯一Id
	 */
	private String serverId;
	
	/**
	 * 服务类型
	 */
	private String serverType;
	
	/**
	 * 服务Ip
	 */
	private String serverIp;
	
	/**
	 * 服务端口
	 */
	private String serverPort;
	
	/**
	 * 服务上下文
	 */
	private String serverContext;
	
	/**
	 * 通知状态
	 *client_has_data/client_no_data/server_has_data/server_no_data
	 */
	private String notifyStatus;
	
	/**
	 * xml的客户端配置
	 */
	private XmlClient xmlClient;
	/**
	 * xml的服务端配置
	 */
	private XmlServer xmlServer;

	public String getServerType() {
		return serverType;
	}

	public void setServerType(String serverType) {
		this.serverType = serverType;
	}

	public String getServerPort() {
		return serverPort;
	}

	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}

	public String getServerContext() {
		return serverContext;
	}

	public void setServerContext(String serverContext) {
		this.serverContext = serverContext;
	}

	public XmlClient getXmlClient() {
		return xmlClient;
	}

	public void setXmlClient(XmlClient xmlClient) {
		this.xmlClient = xmlClient;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public XmlServer getXmlServer() {
		return xmlServer;
	}

	public void setXmlServer(XmlServer xmlServer) {
		this.xmlServer = xmlServer;
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getNotifyStatus() {
		return notifyStatus;
	}

	public void setNotifyStatus(String notifyStatus) {
		this.notifyStatus = notifyStatus;
	}
}
