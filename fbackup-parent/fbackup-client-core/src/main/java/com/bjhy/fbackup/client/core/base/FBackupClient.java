package com.bjhy.fbackup.client.core.base;

import com.bjhy.fbackup.common.domain.DirectoryInfo;
import com.bjhy.fbackup.common.domain.XmlFbackup;

/**
 * 文件备份客户端处理接口
 * @author wulin
 *
 */
public interface FBackupClient {
	
	/**
	 * 返回目录类型
	 * @return
	 */
	String directoryType();
	
	/**
	 * 处理资源
	 * @param directory 目录信息
	 * @param fbackup 配置文件信息
	 */
	void dealWithResource(DirectoryInfo directoryInfo,XmlFbackup fbackup);

}
