package com.bjhy.fbackup.common.zookeeper;

import org.apache.zookeeper.WatchedEvent;

/**
 * zookeeper的各种事件接口
 * 使用 curator 包的类来替代
 * @author wubo
 *
 */
@Deprecated
public interface ZookeeperEvent {
	
	/**
	 * 连接事件
	 * @param event
	 * @return
	 */
	void connect(WatchedEvent event);
	
	/**
	 * 节点被创建事件
	 * @param event
	 * @return
	 */
	void nodeCreated(WatchedEvent event);
	
	/**
	 * 节点Path被改变事件
	 * @param event
	 */
	void nodeChildrenChanged(WatchedEvent event);
	
	/**
	 * 节点数据改变事件
	 * @param event
	 */
	void nodeDataChanged(WatchedEvent event);
	
	/**
	 * 节点被删除事件
	 * @param event
	 */
	void nodeDeleted(WatchedEvent event);
}
