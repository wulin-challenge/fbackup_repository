package com.bjhy.fbackup.common.domain;

import java.io.Serializable;

/**
 * 目录信息
 * @author wulin
 *
 */
public class DirectoryInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/**
	 * 目录类型
	 */
	private String directoryType;
	
	/**
	 * 目录内容
	 */
	private String content;
	
	/**
	 * 可自定义的扩展字段
	 */
	private String customField;
	
	public DirectoryInfo() {
	}

	public DirectoryInfo(String directoryType, String content, String customField) {
		super();
		this.directoryType = directoryType;
		this.content = content;
		this.customField = customField;
	}

	public String getDirectoryType() {
		return directoryType;
	}

	public void setDirectoryType(String directoryType) {
		this.directoryType = directoryType;
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
}
