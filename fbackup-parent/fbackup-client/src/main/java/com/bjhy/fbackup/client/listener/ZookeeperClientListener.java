package com.bjhy.fbackup.client.listener;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.zookeeper.CreateMode;

import com.bjhy.fbackup.client.util.ClientVersionUtil;
import com.bjhy.fbackup.common.annotation.FBackupListener;
import com.bjhy.fbackup.common.domain.XmlFbackup;
import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.common.util.LoggerUtils;
import com.bjhy.fbackup.common.util.SeriUtil;
import com.bjhy.fbackup.common.util.ZookepperUtil;
import com.bjhy.fbackup.common.zookeeper.curator.ZookeeperCuratorConfig;
import com.bjhy.fbackup.common.zookeeper.curator.ZookeeperCuratorEvent;

@FBackupListener
public class ZookeeperClientListener implements ZookeeperCuratorEvent{
	
	
	@Override
	public void nodeAddedEvent(CuratorFramework curatorFramework,TreeCacheEvent treeCacheEvent) {
	}
	
	@Override
	public void nodeUpdatedEvent(CuratorFramework curatorFramework,TreeCacheEvent treeCacheEvent) {
		String path = treeCacheEvent.getData().getPath();
		byte[] data = treeCacheEvent.getData().getData();
		ClientVersionUtil.dealWithVersion(path, data);
	}

	@Override
	public void nodeRemovedEvent(CuratorFramework curatorFramework,TreeCacheEvent treeCacheEvent) {
		
	}

	@Override
	public void connectionReconnectedEvent(CuratorFramework curatorFramework,TreeCacheEvent treeCacheEvent) {
		//zookeeper重新连接的事件
		ZookepperUtil.connectionReconnectedEvent(curatorFramework, treeCacheEvent);
	}
	
}
