package com.bjhy.fbackup.server.core.base;

import com.bjhy.fbackup.common.domain.ClientFileTransfer;
import com.bjhy.fbackup.common.domain.DirectoryInfo;
import com.bjhy.fbackup.common.domain.XmlFbackup;

/**
 * 文件备份服务端处理接口
 * @author wulin
 *
 */
public interface FBackupServer {
	
	/**
	 * 返回目录类型
	 * @return
	 */
	String directoryType();
	
	/**
	 * 处理文件
	 * @param clientFileTransfer 客户端文件传输实体
	 * @param clientFileTransfer 服务端的xml配置文件的跟元素
	 * @param directoryInfo 当前正在传输文件所对饮的目录信息
	 */
	void dealWithResource(ClientFileTransfer clientFileTransfer,XmlFbackup xmlFbackupServer,DirectoryInfo directoryInfo);
}
