package com.bjhy.fbackup.common.file.watcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.LinkedList;
import java.util.List;

import com.bjhy.fbackup.common.domain.DirectoryInfo;
import com.bjhy.fbackup.common.util.FileUtil;

/**
 * 文件观察者
 * @author wubo
 *
 */
public class FileWatcher {
	
	/**
	 * 传输处理
	 */
	private TransferDealWith transferDealWith;
	
	/**
	 * 是否销毁监听对象
	 */
	private Boolean destroy = false;
	
	/**
	 *观察目录信息
	 */
	private DirectoryInfo watcherDirectoryInfo;
	
	private WatchService watcher;
	
	public FileWatcher(DirectoryInfo directoryInfo,TransferDealWith transferDealWith) {
		super();
		this.watcherDirectoryInfo = directoryInfo;
		this.transferDealWith = transferDealWith;
		fileWatcherRegister();
	}

	/**
	 * 文件目录观察者注册
	 * @param watcherDirectory
	 */
	private void fileWatcherRegister(){
		
		try {
			watcher = FileSystems.getDefault().newWatchService();
			
			//这是要监听的主目录
			registerFileListener(FileUtil.replaceSpritAndNotEnd(watcherDirectoryInfo.getContent()));
			
			//注册监听子目录
			registerSubDirectoryListener();

			//事件的驱动
			while (true) {
				WatchKey watckKey = watcher.take(); //阻塞队列,当监听的文件目录没有任何改变时,就阻塞在这里
				List<WatchEvent<?>> events = watckKey.pollEvents();
				
				//当 destroy 为true表示销毁监听文件对象
				if(destroy){
					break;
				}
				for (WatchEvent<?> event : events) {
					if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) { //当文件被创建时,被监听的事件
						fileCreateEvent(watckKey,event);
					}
					
					if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) { //当文件被更改时,被监听的事件
						fileModifyEvent(watckKey,event);
					}
					
					if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) { //当文件被删除时,被监听的事件
						fileDeleteEvent(watckKey,event);
					}
				}
				if (!watckKey.reset()) { //重置
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 注册监听子目录
	 * @throws IOException
	 */
	private void registerSubDirectoryListener() throws IOException {
		File subDirectory = new File(FileUtil.replaceSpritAndNotEnd(watcherDirectoryInfo.getContent()));
		LinkedList<File> fList = new LinkedList<File>();
		fList.addLast(subDirectory);
		while (fList.size() > 0 ) {
		    File f = fList.removeFirst();
		    if(f.listFiles() == null)
		        continue;
		    for(File file2 : f.listFiles()){
		            if (file2.isDirectory()){//下一级目录
		            fList.addLast(file2);
		            //依次注册子目录
		            registerFileListener(file2.getAbsolutePath());////依次注册子目录
		        }
		    }
		}
	}

	/**
	 * 注册文件监听器
	 * @param fileDirectory
	 * @throws IOException
	 */
	private void registerFileListener(String fileDirectory) throws IOException {
		File newCreateFile = new File(fileDirectory);
		if(newCreateFile.isDirectory()){
			 //依次注册子目录
		    Paths.get(fileDirectory).register(watcher 
		            , StandardWatchEventKinds.ENTRY_CREATE
		            , StandardWatchEventKinds.ENTRY_MODIFY
		            , StandardWatchEventKinds.ENTRY_DELETE);
		}
	}
	
	/**
	 * 文件被创建事件
	 * @param event
	 */
	private void fileCreateEvent(WatchKey watckKey,WatchEvent<?> event){
		String filePathOfFileWatcher = FileUtil.getFilePathOfFileWatcher(watckKey, event);
		try {
			registerFileListener(filePathOfFileWatcher);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		transferDealWith.fileCreateEvent(watcherDirectoryInfo,watckKey, event);
	}
	
	/**
	 * 文件被更改事件
	 * @param event
	 */
	private void fileModifyEvent(WatchKey watckKey,WatchEvent<?> event){
		transferDealWith.fileModifyEvent(watcherDirectoryInfo,watckKey,event);
	}
	
	/**
	 * 文件被删除事件
	 * @param event
	 */
	private void fileDeleteEvent(WatchKey watckKey,WatchEvent<?> event){
		transferDealWith.fileDeleteEvent(watcherDirectoryInfo,watckKey,event);
	}

	public Boolean getDestroy() {
		return destroy;
	}

	public void setDestroy(Boolean destroy) {
		this.destroy = destroy;
	}
	
}
