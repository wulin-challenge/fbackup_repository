package com.bjhy.fbackup.client.base;

import java.util.List;

import com.bjhy.fbackup.common.domain.XmlFbackup;
import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.common.file.watcher.TransferDealWithConfig;
import com.bjhy.fbackup.common.register.LoadResourceRegisterCurator;
import com.bjhy.fbackup.common.store.derby.DerbyDatabase;
import com.bjhy.fbackup.common.store.derby.InitRepository;

public class BaseClient {
	
	/**
	 * 资源对象
	 */
	private XmlFbackup xmlFbackup;
	
	/**
	 * 客户端加载资源
	 */
	public void baseClientLoaderResources(){
		new LoadResourceRegisterCurator().loadXmlRegisterZookeeper();
		xmlFbackup = ExtensionLoader.getInstance(XmlFbackup.class);
		
		DerbyDatabase.initDerby("fbackup_client_derby");//创建嵌入式数据库,数据库名称
		InitRepository.loadEntityTable(); //创建表
		dealWithListener();//处理监听
		BaseScheduled.getInstance().excutorVersionScheduled();//执行版本扫描定时器
	}
	
	/**
	 * 处理监听
	 */
	private void dealWithListener(){
		//这是文件监听器注册
		List<String> databaseList = ClientStorePath.getDatabaseList();
		if(databaseList != null){
			for (String watcherDirectory : databaseList) {
				Thread thread = new Thread(){
					@Override
					public void run() {
						TransferDealWithConfig transferDealWithConfig = new TransferDealWithConfig();
						transferDealWithConfig.registerFileWatcher(watcherDirectory);
					}
				};
				thread.start();
			}
		}
		
		BaseClientCore baseClientCore =new BaseClientCore();
		ExtensionLoader.setInstance(BaseClientCore.class, baseClientCore);
		baseClientCore.pictureAndDatabaseListener();
	}
	
}
