package com.bjhy.fbackup.common.file.watcher;

import com.bjhy.fbackup.common.domain.DirectoryInfo;

/**
 * 传输处理的配置
 * @author wubo
 */
public class TransferDealWithConfig {
	/**
	 * 注册文件监听器
	 * @param watcherDirectory 注册的根路径
	 */
	public void registerFileWatcher(DirectoryInfo directoryInfo){
		TransferDealWith transferDealWith = new TransferDealWithAdapter();
//		new FileWatcher(FileUtil.replaceSpritAndNotEnd(watcherDirectory), transferDealWith);
		new FileWatcher(directoryInfo, transferDealWith);
	}

}
