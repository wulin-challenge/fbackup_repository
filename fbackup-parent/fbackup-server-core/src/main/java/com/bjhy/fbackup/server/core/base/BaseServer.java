package com.bjhy.fbackup.server.core.base;

import com.bjhy.fbackup.common.domain.XmlFbackup;
import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.common.register.LoadResourceRegisterCurator;
import com.bjhy.fbackup.server.core.util.ServerCenterUtil;

import cn.wulin.brace.remoting.RemotingException;
import cn.wulin.brace.telnet.TelnetServers;
import cn.wulin.ioc.logging.Logger;
import cn.wulin.ioc.logging.LoggerFactory;

public class BaseServer {
	private Logger logger = LoggerFactory.getLogger(BaseServer.class);
	
	private XmlFbackup xmlFbackup;
	
	/**
	 * 服务端加载资源
	 */
	public void baseServerLoaderResources(){
		try {
			TelnetServers.bind();
		} catch (RemotingException e) {
			logger.error(e.getMessage(),e);
		}
		new LoadResourceRegisterCurator().loadXmlRegisterZookeeper();
		xmlFbackup = ExtensionLoader.getInstance(XmlFbackup.class);
		
		BaseServerClientConsumer baseServerClientConsumer = new BaseServerClientConsumer();
		ExtensionLoader.setInstance(BaseServerClientConsumer.class, baseServerClientConsumer);
		baseServerClientConsumer.executorConsumer();
		
		ServerCenterUtil.checkSyncVersion();// 检测同步的版本
	}
	
	
}
