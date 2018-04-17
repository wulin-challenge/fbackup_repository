package com.bjhy.fbackup.common.zookeeper.curator;

import java.io.IOException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import com.bjhy.fbackup.common.domain.XmlFbackup;
import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.common.util.LoggerUtils;
import com.bjhy.fbackup.common.xml.resources.LoaderXmlResources;

/**
 * zookeeperCurator的配置
 * @author wubo
 *
 */
public class ZookeeperCuratorConfig {
	
	private static final String ROOT_NODE = "/fbackup";//根节点
	public static final String ROOT_NODE_CLIENT = "/fbackup/client";//客户端节点
	public static final String ROOT_NODE_SERVER = "/fbackup/server";//服务端节点
	public static final String ROOT_NODE_VERSION = "/fbackup/version";//版本节点端节点
	public static String CurrentNode;//当前节点
	
	private static ZookeeperCuratorConfig zookeeperCuratorConfig;
	
	private CuratorFramework curatorFramework;
	
	private ZookeeperCuratorConfig(){
		try {
			connectZookeeperCurator();
			createNode(ROOT_NODE_CLIENT, CreateMode.PERSISTENT);
			createNode(ROOT_NODE_SERVER, CreateMode.PERSISTENT);
			setNodeListener(ROOT_NODE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 连接zookeeper
	 * @throws IOException
	 */
	private void connectZookeeperCurator() throws IOException{
		curatorFramework = CuratorFrameworkFactory.newClient(getUrl(), new ExponentialBackoffRetry(getTimeOut(), 3));
		curatorFramework.start();
	}
	
	private String getUrl(){
		XmlFbackup instance = ExtensionLoader.getInstance(XmlFbackup.class);
		
		if(LoaderXmlResources.CLIENT.equalsIgnoreCase(instance.getServerType())){
			return instance.getXmlClient().getZookeeperAddress();
		}else if(LoaderXmlResources.SERVER.equalsIgnoreCase(instance.getServerType())){
			return instance.getXmlServer().getZookeeperAddress();
		}
		return "";
	}
	
	private int getTimeOut(){
		XmlFbackup instance = ExtensionLoader.getInstance(XmlFbackup.class);
		
		if(LoaderXmlResources.CLIENT.equalsIgnoreCase(instance.getServerType())){
			return (int)instance.getXmlClient().getZookeeperTimeout();
		}else if(LoaderXmlResources.SERVER.equalsIgnoreCase(instance.getServerType())){
			return (int)instance.getXmlServer().getZookeeperTimeout();
		}
		return 0;
	}
	
	/**
	 * 判断节点是否存在,存在就返回true,否则返回false
	 * @param path
	 * @return
	 */
	public Boolean existsNode(String path){
		try {
			if(curatorFramework.checkExists().forPath(path) == null){
				return false;
			}
		} catch (Exception e) {
			LoggerUtils.error(path+" 节点创建失败!", e);
		}
		return true;
	}
	
	/**
	 * 创建节点:这里的节点数据为空
	 * @param node 节点
	 * @param createMode 节点模式
	 */
	public void createNode(String path,CreateMode createMode){
		try {
			if(curatorFramework.checkExists().forPath(path) == null){
				curatorFramework.create().creatingParentsIfNeeded().withMode(createMode).forPath(path);
			}
		} catch (Exception e) {
			LoggerUtils.error(path+" 节点创建失败!", e);
		}
	}
	
	/**
	 * 设置节点数据
	 * @param path
	 * @param data
	 */
	public void setNodeData(String path,byte[] data){
		try {
			if(curatorFramework.checkExists().forPath(path) != null){
				curatorFramework.setData().forPath(path, data);
			}
		} catch (Exception e) {
			LoggerUtils.error(path+" 设置节点数据失败!", e);
		}
	}
	
	/**
	 * 获取节点数据
	 * @param path
	 * @return
	 */
	public byte[] getNodeData(String path){
		try {
			if(curatorFramework.checkExists().forPath(path) != null){
				byte[] nodeData = curatorFramework.getData().forPath(path);
				return nodeData;
			}
		} catch (Exception e) {
			LoggerUtils.error(path+" 获取节点数据失败!", e);
		}
		return null;
	}
	
	/**
	 * 删除节点
	 * @param path
	 */
	public void removeNode(String path){
		try {
			if(curatorFramework.checkExists().forPath(path) != null){
				curatorFramework.delete().guaranteed().deletingChildrenIfNeeded().forPath(path);
			}
		} catch (Exception e) {
			LoggerUtils.error(path+" 删除节点失败!", e);
		}
	}
	
	/**
	 * 设置节点监听
	 * @param path
	 */
	@SuppressWarnings("resource")
	private void setNodeListener(String path){
		TreeCache treeCache = new TreeCache(curatorFramework, path);
		
		ZookeeperCuratorEvent zookeeperCuratorEvent = new ZookeeperCuratorEventAdapter();
		TreeCacheListener treeCacheListener = new ZookeeperCuratorListener(zookeeperCuratorEvent);
		treeCache.getListenable().addListener(treeCacheListener);
		try {
			treeCache.start();
		} catch (Exception e) {
			LoggerUtils.error(path+" 节点监听启动失败!", e);
		}
	}
	
	/**
	 * 得到唯一实例并启动zookeeper
	 * @return
	 */
	public static ZookeeperCuratorConfig getInstanceAndStartZookeeper(){
		if(zookeeperCuratorConfig == null){
			zookeeperCuratorConfig = new ZookeeperCuratorConfig();
			ExtensionLoader.setInstance(ZookeeperCuratorConfig.class, zookeeperCuratorConfig);
		}
		return zookeeperCuratorConfig;
	}

	public CuratorFramework getCuratorFramework() {
		return curatorFramework;
	}
}
