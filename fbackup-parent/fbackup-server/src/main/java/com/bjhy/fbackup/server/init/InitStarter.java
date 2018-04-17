package com.bjhy.fbackup.server.init;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.server.base.BaseServer;

/**
 * 初始化启动项
 * @author wubo
 */
@Component
public class InitStarter implements InitializingBean{

	@Override
	public void afterPropertiesSet() throws Exception {
		initBaseClient();// 初始化服务端
	}
	/**
	 * 初始化服务端
	 */
	private void initBaseClient(){
		BaseServer baseClient = new BaseServer();
		ExtensionLoader.setInstance(BaseServer.class, baseClient);
		
		baseClient.baseServerLoaderResources();//加载服务端资源
	}
}
