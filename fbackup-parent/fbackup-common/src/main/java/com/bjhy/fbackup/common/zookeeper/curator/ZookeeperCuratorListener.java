package com.bjhy.fbackup.common.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;

/**
 * 所有子节点监听器
 * @author wubo
 *
 */
public class ZookeeperCuratorListener implements TreeCacheListener{
	
	private ZookeeperCuratorEvent zookeeperCuratorEvent;
	
	public ZookeeperCuratorListener(ZookeeperCuratorEvent zookeeperCuratorEvent){
		this.zookeeperCuratorEvent = zookeeperCuratorEvent;
	}

	@Override
	public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent)throws Exception {
		Type type = treeCacheEvent.getType();
		
		if(Type.NODE_ADDED == type){
			zookeeperCuratorEvent.nodeAddedEvent(curatorFramework, treeCacheEvent);
		}else if(Type.NODE_UPDATED == type){
			zookeeperCuratorEvent.nodeUpdatedEvent(curatorFramework, treeCacheEvent);
		}else if(Type.NODE_REMOVED == type){
			zookeeperCuratorEvent.nodeRemovedEvent(curatorFramework, treeCacheEvent);
		}else if(Type.CONNECTION_RECONNECTED == type){
			zookeeperCuratorEvent.connectionReconnectedEvent(curatorFramework, treeCacheEvent);
		}
	}

}
