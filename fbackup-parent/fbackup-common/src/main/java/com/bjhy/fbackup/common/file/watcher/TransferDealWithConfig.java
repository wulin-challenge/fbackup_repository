package com.bjhy.fbackup.common.file.watcher;

/**
 * 传输处理的配置
 * @author wubo
 */
public class TransferDealWithConfig {
	/**
	 * 注册文件监听器
	 * @param watcherDirectory 注册的根路径
	 */
	public void registerFileWatcher(String watcherDirectory){
		TransferDealWith transferDealWith = new TransferDealWithAdapter();
		new FileWatcher(watcherDirectory, transferDealWith);
	}

}
