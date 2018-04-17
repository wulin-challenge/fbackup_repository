package com.bjhy.fbackup.common.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

/**
 * 处理zookeeper的各种事件等
 * 使用 curator 包的类来替代
 * @author wubo
 *
 */
@Deprecated
public class ZookeeperWatcher implements Watcher{
	
	private ZookeeperCofig zookeeperCofig;
	
	private ZookeeperEvent zookeeperEvent;

	public ZookeeperWatcher(ZookeeperCofig zookeeperCofig,ZookeeperEvent zookeeperEvent){
		this.zookeeperCofig = zookeeperCofig;
		this.zookeeperEvent = zookeeperEvent;
	}
	/**
	 * 真正处理zookeeper的各种事件等
	 */
	@Override
	public void process(WatchedEvent event) {
		
		try {
			String path = event.getPath();
			zookeeperCofig.getZookeeper().getChildren(path, true);
			
		} catch (Exception e) {}//把当前这个异常给吃了
		
		if (event.getType() == Watcher.Event.EventType.None) {
			zookeeperEvent.connect(event);////连接事件
		
		} else if (event.getType() == Watcher.Event.EventType.NodeCreated) {
			zookeeperEvent.nodeCreated(event);////节点创建事件
		
		} else if (event.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
			zookeeperEvent.nodeChildrenChanged(event);//子节点创建更新事件
		} else if (event.getType() == Watcher.Event.EventType.NodeDataChanged) {
			zookeeperEvent.nodeDataChanged(event);//节点数据库更新事件
		
		} else if (event.getType() == Watcher.Event.EventType.NodeDeleted) {
			zookeeperEvent.nodeDeleted(event);//节点被删除事件
		}
	}

}
