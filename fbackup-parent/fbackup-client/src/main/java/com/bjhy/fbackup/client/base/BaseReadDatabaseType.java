package com.bjhy.fbackup.client.base;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.bjhy.fbackup.client.base.ClientStorePath.SingleFile;
import com.bjhy.fbackup.client.dao.FileTransferEntityDao;
import com.bjhy.fbackup.client.domain.FileTransferEntity;
import com.bjhy.fbackup.client.util.InstanceUtil;
import com.bjhy.fbackup.common.annotation.FBackupListener;
import com.bjhy.fbackup.common.domain.XmlFbackup;
import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.common.util.ConstantUtil;
import com.bjhy.fbackup.common.util.FileUtil;
import com.bjhy.fbackup.common.util.LoggerUtils;
import com.bjhy.fbackup.common.util.SeriUtil;
import com.bjhy.fbackup.common.zookeeper.curator.ZookeeperCuratorConfig;

/**
 * 数据库文件的具体实现类型
 * @author wubo
 */
@FBackupListener
public class BaseReadDatabaseType implements ReadDatabaseType{
	
	/**
	 * 资源对象
	 */
	private XmlFbackup xmlFbackup = ExtensionLoader.getInstance(XmlFbackup.class);
	
	/**
	 * 扫描任务(每次只开启一个线程其周期执行)
	 */
	private final ScheduledThreadPoolExecutor sconningTask = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);

	@Override
	public void dealWithDatabase(String directoryType,List<String> databaseList) {
//		List<String> databaseList = ClientStorePath.getDatabaseList();
		if(databaseList != null){
			executeScanningTask();//执行扫描任务
		}
		
	}
	
	/**
	 * 执行扫描任务
	 */
	private void executeScanningTask(){
		
		sconningTask.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					scanningCheckFile();//扫描并检查文件
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 5, 15, TimeUnit.SECONDS);
	}
	
	/**
	 * 扫描并检查文件
	 */
	private void scanningCheckFile(){
		final BaseDatabaseDealWith database = BaseDatabaseDealWith.getInstance();
		ClientStorePath.readFiles(new SingleFile(){
			@Override
			public void oneFile(String fileType,File oneFile,String relativePath) {
				database.checkAndStoreFile(fileType, oneFile,relativePath);//检查并存储文件
			}
		});
		
		Boolean hasTransferData = database.hasTransferData();
		if(hasTransferData){
			database.updateCurrentNode();
		}
	}
}
