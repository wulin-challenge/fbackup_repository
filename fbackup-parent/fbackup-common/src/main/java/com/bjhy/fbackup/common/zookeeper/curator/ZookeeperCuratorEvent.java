package com.bjhy.fbackup.common.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
/**
 * 这里的事件都是某个节点下子节点,是所有节点
 * @author wubo
 *
 */
public interface ZookeeperCuratorEvent {
	
	/**
	 * 节点被添加事件
	 * @param curatorFramework curatorFramework对象:相对与原生的zookeeper对象
	 * @param treeCacheEvent 节点事件对象
	 */
	public void nodeAddedEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent);
	
	/**
	 * 节点被更新事件:这里指的节点数据被更新
	 * @param curatorFramework curatorFramework对象:相对与原生的zookeeper对象
	 * @param treeCacheEvent 节点事件对象
	 */
	public void nodeUpdatedEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent);
	
	/**
	 * 节点被移除事件
	 * @param curatorFramework curatorFramework对象:相对与原生的zookeeper对象
	 * @param treeCacheEvent 节点事件对象
	 */
	public void nodeRemovedEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent);
	
	/**
	 * 重新连接zookeeper成功事件
	 * @param curatorFramework
	 * @param treeCacheEvent
	 */
	public void connectionReconnectedEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent);
}
