package com.bjhy.fbackup.server.base;

import com.bjhy.fbackup.common.domain.XmlFbackup;
import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.common.register.LoadResourceRegisterCurator;
import com.bjhy.fbackup.server.util.ServerCenterUtil;

public class BaseServer {
	
	private XmlFbackup xmlFbackup;
	
	
	/**
	 * 服务端加载资源
	 */
	public void baseServerLoaderResources(){
		new LoadResourceRegisterCurator().loadXmlRegisterZookeeper();
		xmlFbackup = ExtensionLoader.getInstance(XmlFbackup.class);
		
		BaseServerClientConsumer baseServerClientConsumer = new BaseServerClientConsumer();
		ExtensionLoader.setInstance(BaseServerClientConsumer.class, baseServerClientConsumer);
		baseServerClientConsumer.executorConsumer();
		
		ServerCenterUtil.checkSyncVersion();// 检测同步的版本
	}
	
	
}
