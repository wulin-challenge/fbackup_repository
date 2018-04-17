package com.bjhy.fbackup.common.util;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.zookeeper.CreateMode;

import com.bjhy.fbackup.common.domain.XmlFbackup;
import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.common.zookeeper.curator.ZookeeperCuratorConfig;

public class ZookepperUtil {
	
	/**
	 * zookeeper重新连接的事件
	 * @param curatorFramework
	 * @param treeCacheEvent
	 */
	public static void connectionReconnectedEvent(CuratorFramework curatorFramework,TreeCacheEvent treeCacheEvent) {
		String currentNode = ZookeeperCuratorConfig.CurrentNode;
		ZookeeperCuratorConfig zookeeperCuratorConfig = ExtensionLoader.getInstance(ZookeeperCuratorConfig.class);
		Boolean existsNode = zookeeperCuratorConfig.existsNode(currentNode);
		if(!existsNode){
			zookeeperCuratorConfig.createNode(currentNode, CreateMode.EPHEMERAL);
			XmlFbackup xmlFbackup = ExtensionLoader.getInstance(XmlFbackup.class);
			byte[] loadFileXmlByte = SeriUtil.serializeProtoStuffTobyteArray(xmlFbackup, XmlFbackup.class);
			zookeeperCuratorConfig.setNodeData(currentNode, loadFileXmlByte);
			LoggerUtils.info("zookeeper 重新连接成功,zookeeper中当前  "+currentNode+" 节点已经被剔除了,节点被重新创建!");
		}else{
			LoggerUtils.info("zookeeper 重新连接成功,zookeeper中当前  "+currentNode+" 节点依旧存在,没有重新创建节点!");
		}
	}

}
