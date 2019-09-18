package com.bjhy.fbackup.client.domain;

import com.bjhy.fbackup.common.annotation.Column;
import com.bjhy.fbackup.common.annotation.EntityTable;
import com.bjhy.fbackup.common.annotation.Id;

/**
 * 传输映射实体
 * @author wubo
 */
@EntityTable(name="base_transfer_mapping")
public class TransferMappingEntity {
	
	public TransferMappingEntity(){}
	public TransferMappingEntity(String id, String fileTransferId,
			String serverNumber) {
		super();
		this.id = id;
		this.fileTransferId = fileTransferId;
		this.serverNumber = serverNumber;
	}

	@Id
	private String id;
	
	/**
	 * 文件传输的Id
	 */
	@Column(name="fileTransferId",length=45,indexCommand="create index idx_ft_id on base_transfer_mapping(fileTransferId)")
	private String fileTransferId;
	
	/**
	 * 服务端的编号
	 */
	private String serverNumber;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFileTransferId() {
		return fileTransferId;
	}

	public void setFileTransferId(String fileTransferId) {
		this.fileTransferId = fileTransferId;
	}

	public String getServerNumber() {
		return serverNumber;
	}

	public void setServerNumber(String serverNumber) {
		this.serverNumber = serverNumber;
	}
}
