package com.bjhy.fbackup.server.service;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.bjhy.fbackup.common.annotation.FBackupListener;
import com.bjhy.fbackup.common.domain.ClientFileTransfer;
import com.bjhy.fbackup.common.domain.DirectoryInfo;
import com.bjhy.fbackup.common.domain.StaticsReturnEntity;
import com.bjhy.fbackup.common.domain.XmlFbackup;
import com.bjhy.fbackup.common.util.CenterPropUtil;
import com.bjhy.fbackup.common.util.HttpClientUtil;
import com.bjhy.fbackup.common.util.LoggerUtils;
import com.bjhy.fbackup.server.core.base.FBackupServer;
import com.bjhy.fbackup.server.core.util.ClientHttpUtil;

/**
 * 文件的具体实现类型
 * @author wubo
 */
@FBackupListener
public class FbackupPictureService implements FBackupServer{
	
	/**
	 * 图片
	 */
	public static final String DIRECTORY_TYPE_PICTURE = "picture";
	
	/**
	 * 静态代理的url
	 */
	private String staticsAgentUrl = CenterPropUtil.getProperty("statics_agent_url");

	@Override
	public String directoryType() {
		return DIRECTORY_TYPE_PICTURE;
	}

	@Override
	public void dealWithResource(ClientFileTransfer clientFileTransfer, XmlFbackup xmlFbackupServer,
			DirectoryInfo directoryInfo) {
		
		String serverNumber = xmlFbackupServer.getXmlServer().getServerNumber();//服务的编号
		String absoluteFilePath = clientFileTransfer.getAbsoluteFilePath();
		String relativeFilePath = clientFileTransfer.getRelativeFilePath();
		
		try {
			relativeFilePath = URLEncoder.encode(relativeFilePath, "utf-8");
			absoluteFilePath = URLEncoder.encode(absoluteFilePath, "utf-8");
			serverNumber = URLEncoder.encode(serverNumber, "utf-8");
		} catch (Exception e) {
			LoggerUtils.error("编码失败",e);
		}
		XmlFbackup client = clientFileTransfer.getClient();
		
		if(StringUtils.isBlank(clientFileTransfer.getAbsoluteFilePath())){
			return;
		}
		StringBuffer clientHttpUrl = ClientHttpUtil.getClientHttpUrl(client);
		String staticsDownloadUrl = ClientHttpUtil.getStaticsDownloadUrl(client, clientHttpUrl, relativeFilePath, absoluteFilePath,serverNumber);
		
		HttpClientUtil.receiveSingleFile(staticsDownloadUrl, Collections.EMPTY_MAP,new HttpClientUtil().new ReceiveSingleFileCallBack() {
			@Override
			public void fileStreamCallBack(long contentLenght,InputStream fileStream) {
				Map<String,String> params = new HashMap<String,String>();
				params.put("path", clientFileTransfer.getRelativeFilePath());
				String storeStatic3Url = staticsAgentUrl+"/statics/storeStatic3";
				
				try {
					HttpClientUtil.sendSingleFile(storeStatic3Url, fileStream, "", params, StaticsReturnEntity.class);
				} catch (Exception e) {
					LoggerUtils.error("在向静态资源存储 "+clientFileTransfer.getRelativeFilePath()+" 该文件时错,"+e.getMessage());
				}
			}
		});
	}
	
	
}
