package com.bjhy.fbackup.client.core.base;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.OrderComparator;

import com.bjhy.fbackup.client.core.util.ClientFileUtil;
import com.bjhy.fbackup.common.annotation.FBackupListener;
import com.bjhy.fbackup.common.domain.DirectoryInfo;
import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.common.util.ListenerUtil;
import com.bjhy.fbackup.common.util.LoggerUtils;

/**
 * client的核心类
 * @author wubo
 */
public class BaseClientCore {
	
	/**
	 * 得到fbackupClinet接的实现类
	 */
	private static final List<Class<? extends FBackupClient>> fbackupClientList = ListenerUtil.getListenerClass(FBackupClient.class);
	
	/**
	 * 资源监听
	 */
	public void resourceListener(){
		List<DirectoryInfo> directoryList = ClientFileUtil.getFbackup().getXmlClient().getDirectoryList();
		
		for (DirectoryInfo directoryInfo : directoryList) {
			if(StringUtils.isBlank(directoryInfo.getDirectoryType())) {
				continue;
			}
			
			List<FBackupClient> clientList = findByDirectoryType(directoryInfo.getDirectoryType());
			for (FBackupClient fBackupClient : clientList) {
				fBackupClient.dealWithResource(directoryInfo, ClientFileUtil.getFbackup());
			}
		}
	}
	
	/**
	 * 通过目录类型找到备份客户端实例
	 * @param directoryType 目录类型
	 * @return
	 */
	private List<FBackupClient> findByDirectoryType(String directoryType){
		List<FBackupClient> clientList = buildFBackupClientInstances();
		
		//内部实现
		List<FBackupClient> fbackupInternal = new ArrayList<FBackupClient>();
		//非内部实现
		List<FBackupClient> nonFbackupInternal = new ArrayList<FBackupClient>();
		
		for (FBackupClient client : clientList) {
			if(!client.directoryType().equals(directoryType)) {
				continue;
			}
			
			FBackupListener fBackupListener = client.getClass().getAnnotation(FBackupListener.class);
			if(fBackupListener != null){
				boolean internal = fBackupListener.isFbackupInternal();
				if(internal){
					fbackupInternal.add(client);
				}else{
					nonFbackupInternal.add(client);
				}
			}else{
				nonFbackupInternal.add(client);
			}
		}
		//若外部实现不为空,则使用外部实现,否则默认使用内部实现
		if(nonFbackupInternal.size()>0){
			OrderComparator.sort(nonFbackupInternal);
			return nonFbackupInternal;
		}
		OrderComparator.sort(fbackupInternal);
		return fbackupInternal;
	}
	
	
	
	/**
	 * 构建所有 实现 FBackupClient 接口的实例
	 * @return
	 */
	private List<FBackupClient> buildFBackupClientInstances(){
		List<FBackupClient> readPictureTypeInstances = new ArrayList<FBackupClient>();
		for (Class<? extends FBackupClient> clazz : fbackupClientList) {
			FBackupClient instance = ExtensionLoader.getInstance(clazz);
			if(instance == null){
				try {
					instance = clazz.newInstance();
					ExtensionLoader.setInterfaceInstance(clazz, instance);
				} catch (InstantiationException e) {
					LoggerUtils.error("InstantiationException 异常", e);
				} catch (IllegalAccessException e) {
					LoggerUtils.error("IllegalAccessException 异常", e);
				}
			}
			readPictureTypeInstances.add(instance);
		}
		return readPictureTypeInstances;
	}
}
