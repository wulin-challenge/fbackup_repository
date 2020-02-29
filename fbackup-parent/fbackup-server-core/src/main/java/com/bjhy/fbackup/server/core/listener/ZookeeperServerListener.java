package com.bjhy.fbackup.server.core.listener;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;

import com.bjhy.fbackup.common.annotation.FBackupListener;
import com.bjhy.fbackup.common.domain.XmlFbackup;
import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.common.util.ConstantUtil;
import com.bjhy.fbackup.common.util.LoggerUtils;
import com.bjhy.fbackup.common.util.SeriUtil;
import com.bjhy.fbackup.common.util.ZookepperUtil;
import com.bjhy.fbackup.common.zookeeper.curator.ZookeeperCuratorConfig;
import com.bjhy.fbackup.common.zookeeper.curator.ZookeeperCuratorEvent;
import com.bjhy.fbackup.server.core.base.BaseServerQueue;
import com.bjhy.fbackup.server.core.util.ServerCenterUtil;

@FBackupListener
public class ZookeeperServerListener implements ZookeeperCuratorEvent{

	@Override
	public void nodeAddedEvent(CuratorFramework curatorFramework,TreeCacheEvent treeCacheEvent) {
		System.out.println();
	}

	@Override
	public void nodeUpdatedEvent(CuratorFramework curatorFramework,TreeCacheEvent treeCacheEvent) {
		String path = treeCacheEvent.getData().getPath();
		if(path.startsWith(ZookeeperCuratorConfig.ROOT_NODE_CLIENT)){
			byte[] data = treeCacheEvent.getData().getData();
			XmlFbackup client = SeriUtil.unserializeProtoStuffToObj(data, XmlFbackup.class);
			if(ConstantUtil.NOTIFY_STATUS_CLIENT_HAS_DATA.equalsIgnoreCase(client.getNotifyStatus())){
				//是否同步当前客户端
				Boolean syncClient = ServerCenterUtil.isSyncClient(client);
				if(syncClient){
					if(!BaseServerQueue.contains(client)){
						BaseServerQueue.putClientQueue(client);
						//记录客户端通知zookeeper日志
						writeClientLog(client);
					}
				}
				
				XmlFbackup client2 = SeriUtil.unserializeProtoStuffToObj(data, XmlFbackup.class);
				client2.setNotifyStatus(ConstantUtil.NOTIFY_STATUS_CLIENT_NO_DATA);
				byte[] clientData = SeriUtil.serializeProtoStuffTobyteArray(client2, XmlFbackup.class);
				ZookeeperCuratorConfig instance = ExtensionLoader.getInstance(ZookeeperCuratorConfig.class);
				instance.setNodeData(path, clientData);
			}
		}
	}
	
	@Override
	public void nodeRemovedEvent(CuratorFramework curatorFramework,TreeCacheEvent treeCacheEvent) {
		System.out.println(treeCacheEvent);
	}

	@Override
	public void connectionReconnectedEvent(CuratorFramework curatorFramework,TreeCacheEvent treeCacheEvent) {
		//zookeeper重新连接的事件
		ZookepperUtil.connectionReconnectedEvent(curatorFramework, treeCacheEvent);
	}
	
	/**
	 * 记录客户端通知zookeeper日志
	 * @param client
	 */
	private void writeClientLog(XmlFbackup client){
		try{
			String clientName = client.getXmlClient().getClientName();
			String clientNumber = client.getXmlClient().getClientNumber();
			String serverId = client.getServerId();
			String zookeeperAddress = client.getXmlClient().getZookeeperAddress();
			LoggerUtils.info("客户端  "+clientName+"("+clientNumber+"),节点Id: "+serverId+" 向zookeeper :"+zookeeperAddress+" 通知成功!");
		}catch(Exception e){
			LoggerUtils.error("记录客户端通知zookeeper日志 失败", e);
		}
	}

}
