package com.bjhy.fbackup.server.base;

import com.bjhy.fbackup.common.domain.ClientFileTransfer;
import com.bjhy.fbackup.common.domain.XmlFbackup;

/**
 * 文件存储的接口
 * @author wubo
 *
 */
public interface FileStoreType {
	
	/**
	 * 处理文件
	 * @param clientFileTransfer
	 */
	void dealWithFile(ClientFileTransfer clientFileTransfer,XmlFbackup xmlFbackupServer);

}
