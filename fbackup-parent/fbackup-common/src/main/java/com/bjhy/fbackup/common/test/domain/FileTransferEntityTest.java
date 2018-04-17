package com.bjhy.fbackup.common.test.domain;

import java.util.Date;

import com.bjhy.fbackup.common.annotation.EntityTable;
import com.bjhy.fbackup.common.annotation.Id;

/**
 * 文件传输实体
 * @author wubo
 */
@EntityTable(name="base_file_transfer_test")
public class FileTransferEntityTest {
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
	 * 文件的路径(相对的路径)
	 */
	private String filePath;
	
	/**
	 * 文件状态(create/update/delete)
	 */
	private String fileStatus;
	
	/**
	 * 文件传输状态(alreadyTransfer/noTransfer)
	 */
	private String fileTransferStatus;
	
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

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileStatus() {
		return fileStatus;
	}

	public void setFileStatus(String fileStatus) {
		this.fileStatus = fileStatus;
	}

	public String getFileTransferStatus() {
		return fileTransferStatus;
	}

	public void setFileTransferStatus(String fileTransferStatus) {
		this.fileTransferStatus = fileTransferStatus;
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
}
