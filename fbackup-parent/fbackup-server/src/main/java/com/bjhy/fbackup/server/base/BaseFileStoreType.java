package com.bjhy.fbackup.server.base;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Collections;

import com.bjhy.fbackup.common.annotation.FBackupListener;
import com.bjhy.fbackup.common.domain.ClientFileTransfer;
import com.bjhy.fbackup.common.domain.XmlFbackup;
import com.bjhy.fbackup.common.util.HttpClientUtil;
import com.bjhy.fbackup.common.util.LoggerUtils;
import com.bjhy.fbackup.server.util.ClientHttpUtil;
import com.bjhy.fbackup.server.util.ServerFileUtil;

@FBackupListener
@SuppressWarnings("unchecked")
public class BaseFileStoreType implements FileStoreType{


	@Override
	public void dealWithFile(final ClientFileTransfer clientFileTransfer,XmlFbackup server) {
		String serverNumber = server.getXmlServer().getServerNumber();//服务的编号
		XmlFbackup client = clientFileTransfer.getClient();
		StringBuffer clientHttpUrl = ClientHttpUtil.getClientHttpUrl(client);
		
		String relativeFilePath = clientFileTransfer.getRelativeFilePath();
		try {
			relativeFilePath = URLEncoder.encode(relativeFilePath, "utf-8");
			serverNumber = URLEncoder.encode(serverNumber, "utf-8");
		} catch (Exception e) {
			LoggerUtils.error("编码失败",e);
		}
		
		String fileDownloadUrl = ClientHttpUtil.getFileDownloadUrl(client, clientHttpUrl,relativeFilePath,serverNumber);
		
		HttpClientUtil.receiveSingleFile(fileDownloadUrl, Collections.EMPTY_MAP,new HttpClientUtil().new ReceiveSingleFileCallBack() {
			@Override
			public void fileStreamCallBack(long contentLenght,InputStream fileStream) {
				ServerFileUtil.fileStore(fileStream, clientFileTransfer);
			}
		});
	}
}
