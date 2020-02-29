package com.bjhy.fbackup.client.core.base;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.bjhy.fbackup.client.core.util.ClientVersionUtil;
import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.common.util.LoggerUtils;
import com.bjhy.fbackup.common.zookeeper.curator.ZookeeperCuratorConfig;

public class BaseScheduled {
	

	/**
	 * version定时器
	 */
	private final ScheduledThreadPoolExecutor versionExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
	
	/**
	 * 执行版本扫描定时器
	 */
	public void excutorVersionScheduled(){
		versionExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					String versionNodePath = ZookeeperCuratorConfig.ROOT_NODE_VERSION;
					ZookeeperCuratorConfig instance = ExtensionLoader.getInstance(ZookeeperCuratorConfig.class);
					byte[] nodeData = instance.getNodeData(versionNodePath);
					if(StringUtils.isNoneBlank(versionNodePath) && nodeData != null){
						ClientVersionUtil.dealWithVersion(versionNodePath, nodeData);
					}
				} catch (Exception e) {
					LoggerUtils.error("excutorVersionScheduled: ",e);
				}
			}
		}, 30, 30, TimeUnit.SECONDS);
	}
	
	public static BaseScheduled getInstance(){
		BaseScheduled baseScheduled = ExtensionLoader.getInstance(BaseScheduled.class);
		if(baseScheduled == null){
			baseScheduled = new BaseScheduled();
			ExtensionLoader.setInstance(BaseScheduled.class, baseScheduled);
		}
		return baseScheduled;
	}
}
