package com.bjhy.fbackup.common.file.watcher;

import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;

/**
 * 传输处理接口
 * @author wubo
 *
 */
public interface TransferDealWith {
	/**
	 * 文件被创建事件
	 * @param event
	 */
	public void fileCreateEvent(WatchKey watckKey,WatchEvent<?> event);
	
	/**
	 * 文件被更改事件
	 * @param event
	 */
	public void fileModifyEvent(WatchKey watckKey,WatchEvent<?> event);
	
	/**
	 * 文件被删除事件
	 * @param event
	 */
	public void fileDeleteEvent(WatchKey watckKey,WatchEvent<?> event);
}
