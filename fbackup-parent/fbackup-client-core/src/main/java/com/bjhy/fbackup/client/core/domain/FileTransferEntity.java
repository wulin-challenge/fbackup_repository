package com.bjhy.fbackup.client.core.domain;

import java.util.Date;

import com.bjhy.fbackup.common.annotation.EntityTable;
import com.bjhy.fbackup.common.annotation.Id;

/**
 * 文件传输实体
 * @author wubo
 */
@EntityTable(name="base_file_transfer")
public class FileTransferEntity {
	/**
	 * 文件id
	 */
	@Id
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
	 * 文件传输时间
	 */
	private Date fileTransferTime;
	
	/**
	 * 服务类型
	 */
	private String serverType;
	
	/**
	 * 服务ip
	 */
	private String serverIp;
	
	/**
	 * 目录类型
	 */
	private String directoryType;
	
	/**
	 * 目录类型
	 */
	private String content;
	
	/**
	 * 自定义扩展字段
	 */
	private String customField;
	
	/**
	 * 客户端名称
	 */
	private String clientName;
	
	/**
	 * 客户端编号
	 */
	private String clientNumber;

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

	public Date getFileTransferTime() {
		return fileTransferTime;
	}

	public void setFileTransferTime(Date fileTransferTime) {
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCustomField() {
		return customField;
	}

	public void setCustomField(String customField) {
		this.customField = customField;
	}
	
//	/**
//	 * 文件传输状态(alreadyTransfer/transfering/noTransfer)
//	 */
//	private String fileTransferStatus;
//	/**
//	 * 文件传输状态(alreadyTransfer/noTransfer)
//	 */
//	public String getFileTransferStatus() {
//		return fileTransferStatus;
//	}
//
//	/**
//	 * 文件传输状态(alreadyTransfer/transfering/noTransfer)
//	 */
//	public void setFileTransferStatus(String fileTransferStatus) {
//		this.fileTransferStatus = fileTransferStatus;
//	}
}
