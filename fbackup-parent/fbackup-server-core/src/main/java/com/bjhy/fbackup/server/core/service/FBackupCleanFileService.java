package com.bjhy.fbackup.server.core.service;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.bjhy.fbackup.common.annotation.FBackupListener;
import com.bjhy.fbackup.common.domain.ClientFileTransfer;
import com.bjhy.fbackup.common.domain.DirectoryInfo;
import com.bjhy.fbackup.common.domain.XmlFbackup;
import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.common.util.LoggerUtils;
import com.bjhy.fbackup.server.core.util.ServerFileUtil;

/**
 * 在 服务端配置 custom-field="yes:n" , 表示是文件类型中的要清理文件类型,并对其进行清理
 * <p> yes: 表示要清理 n天前的文件
 * <p> n: 表示清理n天之前的文件
 * @author wulin
 *
 */
@FBackupListener
public class FBackupCleanFileService extends BaseFileService{
	
	private static final String fileType = "yes:0";
	private static volatile boolean isInitScheduled = false;
	
	/**
	 * 清理定时器
	 */
	private final ScheduledThreadPoolExecutor cleanExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
	
	@Override
	public void doDealWithResource(ClientFileTransfer clientFileTransfer, XmlFbackup xmlFbackupServer,
			DirectoryInfo directoryInfo,long contentLenght, InputStream is) {
		//初始化定时器
		initScheduled(directoryInfo);
		ServerFileUtil.fileStoreBySpecifyDate(directoryInfo.getContent(), is, clientFileTransfer);
	}

	@Override
	public String getCustomFileType() {
		return "yes";
	}
	
	/**
	 * 初始化定时器
	 * @param directoryInfo
	 */
	private void initScheduled(DirectoryInfo directoryInfo) {
		if(!isInitScheduled) {
			synchronized (this) {
				if(!isInitScheduled) {
					String numberString = directoryInfo.getCustomField().split(":")[1];
					int numberDay = Integer.parseInt(numberString);
					excutorCleanFile(numberDay, directoryInfo.getContent());
					isInitScheduled = true;
				}
			}
		}
	}
	
	/**
	 * 执行清理n天前的文件目录
	 */
	private void excutorCleanFile(int numberDay,String rootDirectory){
		cleanExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					ServerFileUtil.cleanSpecifyDayDirectory(numberDay, rootDirectory);
				} catch (Exception e) {
					LoggerUtils.error("excutorCleanFile: ",e);
				}
			}
		}, 30, 30, TimeUnit.SECONDS);
	}
}
