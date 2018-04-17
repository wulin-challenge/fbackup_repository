package com.bjhy.fbackup.common.zookeeper.curator;

import java.util.ArrayList;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;

import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.common.util.ListenerUtil;
import com.bjhy.fbackup.common.util.LoggerUtils;

public class ZookeeperCuratorEventAdapter implements ZookeeperCuratorEvent{
	
	private static final List<Class<? extends ZookeeperCuratorEvent>> zookeeperCuratorEventList = ListenerUtil.getListenerClass(ZookeeperCuratorEvent.class);

	@Override
	public void nodeAddedEvent(CuratorFramework curatorFramework,TreeCacheEvent treeCacheEvent) {
		List<ZookeeperCuratorEvent> instances = zookeeperCuratorEventInstances();
		for (ZookeeperCuratorEvent zookeeperCuratorEvent : instances) {
			zookeeperCuratorEvent.nodeAddedEvent(curatorFramework, treeCacheEvent);
		}
	}

	@Override
	public void nodeUpdatedEvent(CuratorFramework curatorFramework,TreeCacheEvent treeCacheEvent) {
		List<ZookeeperCuratorEvent> instances = zookeeperCuratorEventInstances();
		for (ZookeeperCuratorEvent zookeeperCuratorEvent : instances) {
			zookeeperCuratorEvent.nodeUpdatedEvent(curatorFramework, treeCacheEvent);
		}
	}

	@Override
	public void nodeRemovedEvent(CuratorFramework curatorFramework,TreeCacheEvent treeCacheEvent) {
		List<ZookeeperCuratorEvent> instances = zookeeperCuratorEventInstances();
		for (ZookeeperCuratorEvent zookeeperCuratorEvent : instances) {
			zookeeperCuratorEvent.nodeRemovedEvent(curatorFramework, treeCacheEvent);
		}
	}
	
	@Override
	public void connectionReconnectedEvent(CuratorFramework curatorFramework,TreeCacheEvent treeCacheEvent) {
		List<ZookeeperCuratorEvent> instances = zookeeperCuratorEventInstances();
		for (ZookeeperCuratorEvent zookeeperCuratorEvent : instances) {
			zookeeperCuratorEvent.connectionReconnectedEvent(curatorFramework, treeCacheEvent);
		}
	}
	
	/**
	 * 得到所有 实现 ZookeeperCuratorEvent 接口的实例
	 * @return
	 */
	private List<ZookeeperCuratorEvent> zookeeperCuratorEventInstances(){
		List<ZookeeperCuratorEvent> zookeeperCuratorEventInstances = new ArrayList<ZookeeperCuratorEvent>();
		for (Class<? extends ZookeeperCuratorEvent> clazz : zookeeperCuratorEventList) {
			ZookeeperCuratorEvent instance = ExtensionLoader.getInstance(clazz);
			if(instance == null){
				try {
					instance = clazz.newInstance();
					ExtensionLoader.setInterfaceInstance(clazz, instance);
				} catch (InstantiationException e) {
					LoggerUtils.error("InstantiationException 异常", e);
				} catch (IllegalAccessException e) {
					LoggerUtils.error("IllegalAccessException 异常", e);
				}
			}
			zookeeperCuratorEventInstances.add(instance);
		}
		return zookeeperCuratorEventInstances;
	}

}
