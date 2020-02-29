package com.bjhy.fbackup.server.core.service;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Collections;

import com.bjhy.fbackup.common.domain.ClientFileTransfer;
import com.bjhy.fbackup.common.domain.DirectoryInfo;
import com.bjhy.fbackup.common.domain.XmlClient;
import com.bjhy.fbackup.common.domain.XmlFbackup;
import com.bjhy.fbackup.common.util.HttpClientUtil;
import com.bjhy.fbackup.common.util.LoggerUtils;
import com.bjhy.fbackup.server.core.base.FBackupServer;
import com.bjhy.fbackup.server.core.util.ClientHttpUtil;

/**
 * 文件类型服务的基类
 * @author wulin
 *
 */
public abstract class BaseFileService implements FBackupServer{

	@Override
	public String directoryType() {
		return XmlClient.DIRECTORY_TYPE_FILE;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void dealWithResource(ClientFileTransfer clientFileTransfer, XmlFbackup xmlFbackupServer,DirectoryInfo directoryInfo) {
		//判断是否执行该回调
		if(!isExecute(directoryInfo)) {
			return;
		}
		
		//判断是否执行该实例
		if(!isExecuteInstance(clientFileTransfer)) {
			return;
		}
		
		String serverNumber = xmlFbackupServer.getXmlServer().getServerNumber();//服务的编号
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
				BaseFileService.this.doDealWithResource(clientFileTransfer, xmlFbackupServer,directoryInfo, contentLenght, fileStream);
			}
		});
	}
	
	/**
	 * 处理资源
	 * @param clientFileTransfer
	 * @param xmlFbackupServer
	 * @param contentLenght
	 * @param fileStream
	 */
	public abstract void doDealWithResource(ClientFileTransfer clientFileTransfer, XmlFbackup xmlFbackupServer,DirectoryInfo directoryInfo,long contentLenght,InputStream fileStream);
	
	/**
	 * 得到自定义文件类型
	 * @return
	 */
	public abstract String getCustomFileType();
	
	/**
	 * 判断是否执行该回调
	 * @param clientFileTransfer
	 * @param xmlFbackupServer
	 * @return
	 */
	private boolean isExecute(DirectoryInfo directoryInfo) {
		String fileType = getFileType(directoryInfo);
		if(fileType.equals(getCustomFileType())) {
			return true;
		}
		return false;
	}
	
	/**
	 * 是否执行该实例
	 * @param clientFileTransfer
	 * @param xmlFbackupServer
	 * @return
	 */
	private boolean isExecuteInstance(ClientFileTransfer clientFileTransfer) {
		String fileType = getCustomFileType();
		
		//这样写是为了有根好的兼容兼容
		String customField = clientFileTransfer.getCustomField().split(":")[0].trim();
		
		if(fileType.equals(customField)) {
			return true;
		}
		return false;
	}
	
	private String getFileType(DirectoryInfo directoryInfo) {
		String[] fileType = directoryInfo.getCustomField().split(":");
		if(fileType.length == 2) {
			return fileType[0];
		}else {
			throw new RuntimeException(directoryInfo.getCustomField()+" 配置格式错误,正取格式为 yes:n / no:0 ,其中n表示要清理的天数");
		}
	}
}
