package com.bjhy.fbackup.client.core.listener;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;

import com.bjhy.fbackup.client.core.util.ClientVersionUtil;
import com.bjhy.fbackup.common.annotation.FBackupListener;
import com.bjhy.fbackup.common.util.ZookepperUtil;
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
