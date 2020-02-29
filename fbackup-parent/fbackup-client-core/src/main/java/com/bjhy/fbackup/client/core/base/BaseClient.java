package com.bjhy.fbackup.client.core.base;

import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.common.register.LoadResourceRegisterCurator;
import com.bjhy.fbackup.common.store.derby.DerbyDatabase;
import com.bjhy.fbackup.common.store.derby.InitRepository;

public class BaseClient {
	/**
	 * 客户端加载资源
	 */
	public void baseClientLoaderResources(){
		new LoadResourceRegisterCurator().loadXmlRegisterZookeeper();
		
		DerbyDatabase.initDerby("fbackup_client_derby");//创建嵌入式数据库,数据库名称
		InitRepository.loadEntityTable(); //创建表
		dealWithListener();//处理监听
		BaseScheduled.getInstance().excutorVersionScheduled();//执行版本扫描定时器
	}
	
	/**
	 * 处理监听
	 */
	private void dealWithListener(){
		BaseClientCore baseClientCore =new BaseClientCore();
		ExtensionLoader.setInstance(BaseClientCore.class, baseClientCore);
		baseClientCore.resourceListener();
	}
	
}
