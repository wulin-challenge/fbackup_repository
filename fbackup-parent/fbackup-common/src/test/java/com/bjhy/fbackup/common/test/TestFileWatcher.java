package com.bjhy.fbackup.common.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;

import org.apache.commons.io.monitor.FileAlterationListener;

import com.bjhy.fbackup.common.file.watcher.FileWatcher;
import com.bjhy.fbackup.common.file.watcher.TransferDealWith;
import com.bjhy.fbackup.common.util.FileUtil;

/**
 * 测试文件监听器
 * @author wubo
 *
 */
public class TestFileWatcher {
	
	public static void main(String[] args) throws IOException {
		
		String directory = "D:/temp/fbackup/fileWatcher";
		
		TransferDealWith transferDealWith = new FileWatcherImpl();
		FileWatcher fileWatcher2 = new FileWatcher(directory,transferDealWith);
		System.out.println(fileWatcher2);
		System.in.read();
	}
	
	public static class FileWatcherImpl implements TransferDealWith{

		@Override
		public void fileCreateEvent(WatchKey watckKey, WatchEvent<?> event) {
			update("create", watckKey, event);
			System.out.println();
//			FileObserve
//			FileAlterationListener
		}

		@Override
		public void fileModifyEvent(WatchKey watckKey, WatchEvent<?> event) {
			update("modify", watckKey, event);
			System.out.println();
		}

		@Override
		public void fileDeleteEvent(WatchKey watckKey, WatchEvent<?> event) {
//			update("delete", watckKey, event);
			System.out.println();
		}
		
		private void update(String eventType,WatchKey watckKey,WatchEvent<?> event){
			String filePath = FileUtil.getFilePathOfFileWatcher(watckKey, event);
			File file = new File(filePath);
			if(!file.isDirectory()){
				System.out.println(filePath);
			}
		}
		
	}

}
