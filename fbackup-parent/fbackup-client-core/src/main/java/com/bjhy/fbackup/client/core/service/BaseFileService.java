package com.bjhy.fbackup.client.core.service;

import java.util.ArrayList;
import java.util.List;

import com.bjhy.fbackup.client.core.base.FBackupClient;
import com.bjhy.fbackup.common.domain.DirectoryInfo;
import com.bjhy.fbackup.common.domain.XmlClient;
import com.bjhy.fbackup.common.domain.XmlFbackup;
import com.bjhy.fbackup.common.file.watcher.TransferDealWithConfig;

import cn.wulin.thread.expire.thread.ThreadFactoryImpl;

/**
 * 默认的文件处理基类
 * @author wulin
 *
 */
public abstract class BaseFileService implements FBackupClient{
	private ThreadFactoryImpl threadFactory = new ThreadFactoryImpl("BaseFileService");
	
	@Override
	public String directoryType() {
		return XmlClient.DIRECTORY_TYPE_FILE;
	}

	@Override
	public void dealWithResource(DirectoryInfo directoryInfo, XmlFbackup fbackup) {
		//处理文件监听
		dealwithListener(directoryInfo, fbackup);
		
		doDealWithResource(directoryInfo, fbackup);
	}

	/**
	 * 真正处理资源 
	 * @param directoryInfo 目录信息
	 * @param fbackup 配置文件信息
	 */
	public abstract void doDealWithResource(DirectoryInfo directoryInfo, XmlFbackup fbackup);
	
	/**
	 * 处理文件监听
	 * @param directoryInfo 目录信息
	 * @param fbackup
	 */
	private void dealwithListener(DirectoryInfo directoryInfo, XmlFbackup fbackup) {
		//这是文件监听器注册
		Thread thread = threadFactory.newThread(new Runnable() {
			
			@Override
			public void run() {
				TransferDealWithConfig transferDealWithConfig = new TransferDealWithConfig();
				transferDealWithConfig.registerFileWatcher(directoryInfo);
			}
		});
		thread.start();
	}
	
	/**
	 * 得到文件类型的所有目录信息
	 * @return
	 */
	protected List<DirectoryInfo> getFileDirectoryInfoList(String directoryType,XmlFbackup fbackup){
		List<DirectoryInfo> list = new ArrayList<DirectoryInfo>();
		List<DirectoryInfo> directoryList = fbackup.getXmlClient().getDirectoryList();
		
		for (DirectoryInfo directoryInfo : directoryList) {
			if(directoryType.equals(directoryInfo.getDirectoryType())) {
				list.add(directoryInfo);
			}
		}
		return list;
	}
	
	/**
	 * 得到文件类型的所有目录
	 * @return
	 */
	protected List<String> getFileDirectoryList(String directoryType,XmlFbackup fbackup){
		List<String> list = new ArrayList<String>();
		List<DirectoryInfo> directoryList = fbackup.getXmlClient().getDirectoryList();
		
		for (DirectoryInfo directoryInfo : directoryList) {
			if(directoryType.equals(directoryInfo.getDirectoryType())) {
				list.add(directoryInfo.getContent());
			}
			
		}
		return list;
	}
}
