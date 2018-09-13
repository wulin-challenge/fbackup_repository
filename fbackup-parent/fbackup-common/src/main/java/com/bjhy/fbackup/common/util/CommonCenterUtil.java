package com.bjhy.fbackup.common.util;

public class CommonCenterUtil {
	
	/**
	 * 得到传输零字节大小的标记
	 * @return
	 */
	public static boolean getTransferZeroByteFile(){
		Boolean flag = CenterPropUtil.getProperty(ConstantUtil.TRANSFER_ZERO_BYTE_FILE_KEY, true, boolean.class, true);
		return flag;
	}

}
