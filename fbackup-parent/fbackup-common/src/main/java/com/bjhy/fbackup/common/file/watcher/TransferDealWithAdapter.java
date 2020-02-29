package com.bjhy.fbackup.common.file.watcher;

import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.ArrayList;
import java.util.List;

import com.bjhy.fbackup.common.domain.DirectoryInfo;
import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.common.util.ListenerUtil;
import com.bjhy.fbackup.common.util.LoggerUtils;

/**
 * 文件监听的传输处理适配器
 * @author wubo
 *
 */
public class TransferDealWithAdapter implements TransferDealWith{
	
	private static final List<Class<? extends TransferDealWith>> transferDealWithList = ListenerUtil.getListenerClass(TransferDealWith.class);

	@Override
	public void fileCreateEvent(DirectoryInfo directoryInfo,WatchKey watckKey, WatchEvent<?> event) {
		List<TransferDealWith> transferDealWithInstances = transferDealWithInstances();
		for (TransferDealWith transferDealWith : transferDealWithInstances) {
			transferDealWith.fileCreateEvent(directoryInfo,watckKey, event);
		}
	}

	@Override
	public void fileModifyEvent(DirectoryInfo directoryInfo,WatchKey watckKey, WatchEvent<?> event) {
		List<TransferDealWith> transferDealWithInstances = transferDealWithInstances();
		for (TransferDealWith transferDealWith : transferDealWithInstances) {
			transferDealWith.fileModifyEvent(directoryInfo,watckKey, event);
		}
	}

	@Override
	public void fileDeleteEvent(DirectoryInfo directoryInfo,WatchKey watckKey, WatchEvent<?> event) {
		List<TransferDealWith> transferDealWithInstances = transferDealWithInstances();
		for (TransferDealWith transferDealWith : transferDealWithInstances) {
			transferDealWith.fileDeleteEvent(directoryInfo,watckKey, event);
		}
	}
	
	/**
	 * 得到所有 实现 TransferDealWith 接口的实例
	 * @return
	 */
	private List<TransferDealWith> transferDealWithInstances(){
		List<TransferDealWith> transferDealWithInstances = new ArrayList<TransferDealWith>();
		for (Class<? extends TransferDealWith> clazz : transferDealWithList) {
			TransferDealWith instance = ExtensionLoader.getInstance(clazz);
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
			transferDealWithInstances.add(instance);
		}
		return transferDealWithInstances;
	}


}
