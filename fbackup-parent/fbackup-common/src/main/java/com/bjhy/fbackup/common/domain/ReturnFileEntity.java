package com.bjhy.fbackup.common.domain;

import java.io.InputStream;

/**
 * 返回的文件实体
 * @author wubo
 *
 */
public class ReturnFileEntity<T> {
	/**
	 * 返回的文件流
	 */
	private InputStream fileStream;
	
	/**
	 * 返回的参数实体
	 */
	private T returnEntity;

	public InputStream getFileStream() {
		return fileStream;
	}

	public void setFileStream(InputStream fileStream) {
		this.fileStream = fileStream;
	}

	public T getReturnEntity() {
		return returnEntity;
	}

	public void setReturnEntity(T returnEntity) {
		this.returnEntity = returnEntity;
	}
}
