package cn.wulin.thread.expire.thread.util;

import java.util.UUID;

/**
 * 线程工具类
 * @author wubo
 *
 */
public class ThreadUtil {
	
	/**
	 * 得到uuid
	 * @return
	 */
	public static String getUUID(){
		String uuid = UUID.randomUUID().toString().toUpperCase().replace("-", "");
		return uuid;
	}

}
