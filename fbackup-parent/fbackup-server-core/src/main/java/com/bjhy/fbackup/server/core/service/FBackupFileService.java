package com.bjhy.fbackup.server.core.service;

import java.io.InputStream;

import com.bjhy.fbackup.common.annotation.FBackupListener;
import com.bjhy.fbackup.common.domain.ClientFileTransfer;
import com.bjhy.fbackup.common.domain.DirectoryInfo;
import com.bjhy.fbackup.common.domain.XmlFbackup;
import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.server.core.util.ServerFileUtil;

/**
 * 在 服务端配置 custom-field="no:0" , 表示是文件类型中的不清理类型,并不对其进行清理
 * <p> no: 表示文件类型
 * <p> 0: 表示占位服务,以方便解析
 * @author wulin
 *
 */
@FBackupListener
public class FBackupFileService extends BaseFileService{
	
	private static final String fileType = "no:0";
	
	/**
	 * 本身的应用配置
	 */
	private static final XmlFbackup server = ExtensionLoader.getInstance(XmlFbackup.class); 

	@Override
	public void doDealWithResource(ClientFileTransfer clientFileTransfer, XmlFbackup xmlFbackupServer,
			DirectoryInfo directoryInfo,long contentLenght, InputStream is) {
		ServerFileUtil.fileStore(directoryInfo.getContent(), is, clientFileTransfer);
	}

	@Override
	public String getCustomFileType() {
		return "no";
	}
}
