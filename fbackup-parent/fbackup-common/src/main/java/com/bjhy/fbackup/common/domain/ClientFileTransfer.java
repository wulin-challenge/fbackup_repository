package com.bjhy.fbackup.common.domain;
/**
 * 客户端文件传输实体
 * @author wubo
 */
public class ClientFileTransfer {
	/**
	 * 文件id
	 */
	private String id;
	
	/**
	 * 文件名称
	 */
	private String fileName;
	
	/**
	 * 绝对文件路径
	 */
	private String absoluteFilePath;
	
	/**
	 * 相对文件路径
	 */
	private String relativeFilePath;
	
	/**
	 * 文件状态(create/update/delete)
	 */
	private String fileStatus;
	
	/**
	 * 文件传输状态(alreadyTransfer/transfering/noTransfer)
	 */
	private String fileTransferStatus;
	
	/**
	 * 文件传输时间
	 */
	private String fileTransferTime;
	
	/**
	 * 服务类型
	 */
	private String serverType;
	
	/**
	 * 服务ip
	 */
	private String serverIp;
	
	/**
	 * 目录类型(picture/database)
	 */
	private String directoryType;
	
	/**
	 * 客户端名称
	 */
	private String clientName;
	
	/**
	 * 客户端编号
	 */
	private String clientNumber;
	
	/**
	 * 客户端的配置信息
	 */
	private XmlFbackup client;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * 文件状态(create/update/delete)
	 */
	public String getFileStatus() {
		return fileStatus;
	}

	/**
	 * 文件状态(create/update/delete)
	 */
	public void setFileStatus(String fileStatus) {
		this.fileStatus = fileStatus;
	}

	/**
	 * 文件传输状态(alreadyTransfer/noTransfer)
	 */
	public String getFileTransferStatus() {
		return fileTransferStatus;
	}

	/**
	 * 文件传输状态(alreadyTransfer/noTransfer)
	 */
	public void setFileTransferStatus(String fileTransferStatus) {
		this.fileTransferStatus = fileTransferStatus;
	}

	public String getFileTransferTime() {
		return fileTransferTime;
	}

	public void setFileTransferTime(String fileTransferTime) {
		this.fileTransferTime = fileTransferTime;
	}

	public String getServerType() {
		return serverType;
	}

	public void setServerType(String serverType) {
		this.serverType = serverType;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public String getDirectoryType() {
		return directoryType;
	}

	public void setDirectoryType(String directoryType) {
		this.directoryType = directoryType;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getClientNumber() {
		return clientNumber;
	}

	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}

	public String getAbsoluteFilePath() {
		return absoluteFilePath;
	}

	public void setAbsoluteFilePath(String absoluteFilePath) {
		this.absoluteFilePath = absoluteFilePath;
	}

	public String getRelativeFilePath() {
		return relativeFilePath;
	}

	public void setRelativeFilePath(String relativeFilePath) {
		this.relativeFilePath = relativeFilePath;
	}

	public XmlFbackup getClient() {
		return client;
	}

	public void setClient(XmlFbackup client) {
		this.client = client;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(clientName+"("+clientNumber+")的 "+fileName+" 相对路径:("+relativeFilePath+")");
		return sb.toString();
	}
}
