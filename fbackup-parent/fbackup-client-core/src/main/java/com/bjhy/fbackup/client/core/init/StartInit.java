package com.bjhy.fbackup.client.core.init;

import com.bjhy.fbackup.client.core.base.BaseClient;
import com.bjhy.fbackup.common.extension.ExtensionLoader;

/**
 * 启动初始化
 * @author wubo
 *
 */
public class StartInit {
	
	public static void init(){
		StartInit startInit = new StartInit();
		ExtensionLoader.setInstance(StartInit.class, startInit);
		
		startInit.initBaseClient();// 初始化客户端
	}
	
	/**
	 * 初始化客户端
	 */
	private void initBaseClient(){
		BaseClient baseClient = new BaseClient();
		ExtensionLoader.setInstance(BaseClient.class, baseClient);
		
		baseClient.baseClientLoaderResources();//加载客户端资源
	}

}
