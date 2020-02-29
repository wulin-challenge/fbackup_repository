package com.bjhy.fbackup.client.core.service;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.bjhy.fbackup.client.core.util.ClientFileUtil;
import com.bjhy.fbackup.client.core.util.ClientFileUtil.SingleFile;
import com.bjhy.fbackup.common.annotation.FBackupListener;
import com.bjhy.fbackup.common.domain.DirectoryInfo;
import com.bjhy.fbackup.common.domain.XmlFbackup;

import cn.wulin.thread.expire.thread.ThreadFactoryImpl;

/**
 * 文件的具体实现类型
 * @author wubo
 */
@FBackupListener
public class FBackupFileService extends BaseFileService{
	/**
	 * 是否初始化,即 doDealWithResource 方法只能不执行一次
	 */
	private static boolean isInit = false;
	
	/**
	 * 扫描任务(每次只开启一个线程其周期执行)
	 */
	private final ScheduledExecutorService sconningTask = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryImpl("FBackupFileService"));

	@Override
	public void doDealWithResource(DirectoryInfo directoryInfo, XmlFbackup fbackup) {
		//是否初始化,即 doDealWithResource 方法只能不执行一次
		if(isInit) {
			return;
		}
		isInit = true;
		
		String directoryType = directoryInfo.getDirectoryType();
		List<String> fileDirectoryList = getFileDirectoryList(directoryType, fbackup);
		
		if(fileDirectoryList != null && fileDirectoryList.size()>0){
			executeScanningTask(directoryType,fbackup);//执行扫描任务
		}
	}
	
	/**
	 * 执行扫描任务
	 */
	private void executeScanningTask(String directoryType, XmlFbackup fbackup){
		
		sconningTask.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					scanningCheckFile(directoryType,fbackup);//扫描并检查文件
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 5, 15, TimeUnit.SECONDS);
	}
	
	/**
	 * 扫描并检查文件
	 */
	private void scanningCheckFile(String directoryType, XmlFbackup fbackup){
		
		List<DirectoryInfo> directoryInfoList = FBackupFileService.this.getFileDirectoryInfoList(directoryType, fbackup);
		final FBackupFileCoreDealwith database = FBackupFileCoreDealwith.getInstance();
		ClientFileUtil.readFiles(directoryInfoList,new SingleFile(){
			@Override
			public void oneFile(DirectoryInfo directoryInfo,File oneFile,String relativePath) {
				database.checkAndStoreFile(directoryInfo, oneFile,relativePath);//检查并存储文件
			}
		});
		
		Boolean hasTransferData = database.hasTransferData();
		if(hasTransferData){
			database.updateCurrentNode();
		}
	}

}
