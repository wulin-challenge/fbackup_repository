package com.bjhy.fbackup.common.domain;

/**
 * 静态返回实体
 * @author wubo
 *
 */
public class StaticsReturnEntity {
	private String returnStatus;
	private String message;
	public String getReturnStatus() {
		return returnStatus;
	}
	public void setReturnStatus(String returnStatus) {
		this.returnStatus = returnStatus;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
