package com.bjhy.fbackup.server.base;

import com.bjhy.fbackup.common.domain.ClientFileTransfer;
import com.bjhy.fbackup.common.domain.XmlFbackup;

/**
 * 静态资源的存储接口
 * @author wubo
 *
 */
public interface StaticStoreType {
	
	/**
	 * 处理监听资源
	 * @param clientFileTransfer
	 */
	void dealWithStatic(ClientFileTransfer clientFileTransfer,XmlFbackup xmlFbackupServer);

}
