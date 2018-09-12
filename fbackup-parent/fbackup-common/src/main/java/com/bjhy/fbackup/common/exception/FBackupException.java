package com.bjhy.fbackup.common.exception;

/**
 * FBackup异常类
 *
 * @author wubo
 */
public final class FBackupException extends RuntimeException {

    public static final int UNKNOWN_EXCEPTION = 0;//未知异常码
    public static final int NETWORK_EXCEPTION = 1;//网络异常码
    public static final int TIMEOUT_EXCEPTION = 2;//超时异常码
    public static final int BIZ_EXCEPTION = 3;//业务异常码
    public static final int FORBIDDEN_EXCEPTION = 4;//禁用异常码
    public static final int SERIALIZATION_EXCEPTION = 5;//序列化异常码
    public static final int FILE_NO_EXIST = 6;//文件不存在异常
    public static final int FILE_SIZE_IS_ZERO = 7;//文件长度为零异常在异常
    
    private static final long serialVersionUID = 7815426752583648734L;
    private int code; // FBackupException不能有子类，异常类型用ErrorCode表示，以便保持兼容。

    public FBackupException() {
        super();
    }

    public FBackupException(String message, Throwable cause) {
        super(message, cause);
    }

    public FBackupException(String message) {
        super(message);
    }

    public FBackupException(Throwable cause) {
        super(cause);
    }

    public FBackupException(int code) {
        super();
        this.code = code;
    }

    public FBackupException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public FBackupException(int code, String message) {
        super(message);
        this.code = code;
    }

    public FBackupException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isBiz() {
        return code == BIZ_EXCEPTION;
    }

    public boolean isForbidded() {
        return code == FORBIDDEN_EXCEPTION;
    }

    public boolean isTimeout() {
        return code == TIMEOUT_EXCEPTION;
    }

    public boolean isNetwork() {
        return code == NETWORK_EXCEPTION;
    }

    public boolean isSerialization() {
        return code == SERIALIZATION_EXCEPTION;
    }
    
    public boolean isFileNoExist(){
    	return code == FILE_NO_EXIST;
    }
    
    public boolean isFileSizeIsZero(){
    	return code == FILE_SIZE_IS_ZERO;
    }
}