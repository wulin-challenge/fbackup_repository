package com.bjhy.fbackup.common.domain;

import java.io.Serializable;

/**
 * 文件状态
 * 
 * @author wubo
 *
 */
@SuppressWarnings("serial")
public class FileStatus implements Serializable{

	public static final int FILE_NO_EXIST = 6;// 文件不存在异常
	public static final int FILE_SIZE_IS_ZERO = 7;// 文件长度为零异常在异常
	public static final int FILE_NORMAL = 8;// 文件正常
	public static final int UNKNOWN_EXCEPTION = 0; //未知异常

	/**
	 * 文件code
	 */
	private int code = FILE_NORMAL;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public boolean isFileNoExist() {
		return code == FILE_NO_EXIST;
	}

	public boolean isFileSizeIsZero() {
		return code == FILE_SIZE_IS_ZERO;
	}
	
	public boolean isFileNormal(){
		return code == FILE_NORMAL;
	}
	public boolean isUnknownException(){
		return code == UNKNOWN_EXCEPTION;
	}
}
