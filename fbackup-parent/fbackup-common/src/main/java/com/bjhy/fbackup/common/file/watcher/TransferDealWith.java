package com.bjhy.fbackup.common.file.watcher;

import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;

import com.bjhy.fbackup.common.domain.DirectoryInfo;

/**
 * 传输处理接口
 * @author wubo
 *
 */
public interface TransferDealWith {
	/**
	 * 文件被创建事件
	 * @param directoryInfo 所观察目录的目录信息
	 * @param event
	 */
	public void fileCreateEvent(DirectoryInfo directoryInfo,WatchKey watckKey,WatchEvent<?> event);
	
	/**
	 * 文件被更改事件
	 * @param directoryInfo 所观察目录的目录信息
	 * @param event
	 */
	public void fileModifyEvent(DirectoryInfo directoryInfo,WatchKey watckKey,WatchEvent<?> event);
	
	/**
	 * 文件被删除事件
	 * @param directoryInfo 所观察目录的目录信息
	 * @param event
	 */
	public void fileDeleteEvent(DirectoryInfo directoryInfo,WatchKey watckKey,WatchEvent<?> event);
}
