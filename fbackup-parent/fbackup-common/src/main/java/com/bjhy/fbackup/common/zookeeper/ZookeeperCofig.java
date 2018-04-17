package com.bjhy.fbackup.common.zookeeper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import com.bjhy.fbackup.common.domain.XmlFbackup;
import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.common.util.FileUtil;
import com.bjhy.fbackup.common.util.LoggerUtils;
import com.bjhy.fbackup.common.xml.resources.LoaderXmlResources;

/**
 * zookeeper配置 
 * 使用 curator 包的类来替代
 * @author wubo
 *
 */
@Deprecated
public class ZookeeperCofig{
	
	private static ZookeeperCofig zookeeperCofig;
	
	private ZookeeperCofig(){}
	
	/**
	 * 相当于就是用户名(暂时没用)
	 */
	public static final String AUTH_SCHEME = "zoopeeker";
	
	/**
	 * 认证的密码(暂时没用)
	 */
	public static final String AUTH_PASSWORD = "fileBackup";
	
	private static final String ROOT_NODE = "/fbackup";//根节点
	public static final String ROOT_NODE_CLIENT = "/fbackup/client";//客户端节点
	public static final String ROOT_NODE_SERVER = "/fbackup/server";//服务端节点
	
	public static final String ROOT_NODE_CLIENT_IP = "/fbackup/client/ip";//客户端节点ip(是临时序列ip)
	public static final String ROOT_NODE_SERVER_IP = "/fbackup/server/ip";//服务端节点ip(是临时序列ip)
	
//	public static final String ROOT_PERSISTENT_NODE_CLIENT = "/fbackup/persistent_client";//客户端持久化节点
//	public static final String ROOT_PERSISTENT_NODE_SERVER = "/fbackup/persistent_server";//服务端持久化节点
//	
//	public static final String ROOT_EPHEMERAL_NODE_CLIENT = "/fbackup/ephemeral_client";//客户端临时节点
//	public static final String ROOT_EPHEMERAL_NODE_SERVER = "/fbackup/ephemeral_server";//服务端临时节点
	
	private ZooKeeper zookeeper;
	
	private ZookeeperWatcher zookeeperWatcher;

	/**
	 * 连接zookeeper
	 * @throws IOException
	 */
	public void connectZookeeper() throws IOException{
		zookeeper = new ZooKeeper(getUrl(),getTimeOut(),zookeeperWatcher);
		
		//确保一定连接成功
		while (ZooKeeper.States.CONNECTED != zookeeper.getState()) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				LoggerUtils.error("zookeeper连接失败", e);
			}
		}
//		deleteAllPersistentNode(ROOT_NODE);
		createPersistentNode(ROOT_NODE, "这是根节点,管理着服务端和客户端的连接信息");
		
		createPersistentNode(ROOT_NODE_CLIENT, "管理着客户端节点的信息");
		createPersistentNode(ROOT_NODE_SERVER, "管理着服务端节点的信息");
//		
//		createPersistentNode(ROOT_EPHEMERAL_NODE_CLIENT, "管理着客户端临时节点的信息");
//		createPersistentNode(ROOT_EPHEMERAL_NODE_SERVER, "管理着服务端临时节点的信息");
	}
	
	public String getUrl(){
		XmlFbackup instance = ExtensionLoader.getInstance(XmlFbackup.class);
		
		if(LoaderXmlResources.CLIENT.equalsIgnoreCase(instance.getServerType())){
			return instance.getXmlClient().getZookeeperAddress();
		}else if(LoaderXmlResources.SERVER.equalsIgnoreCase(instance.getServerType())){
			return instance.getXmlServer().getZookeeperAddress();
		}
		return "";
	}
	
	public int getTimeOut(){
		XmlFbackup instance = ExtensionLoader.getInstance(XmlFbackup.class);
		
		if(LoaderXmlResources.CLIENT.equalsIgnoreCase(instance.getServerType())){
			return (int)instance.getXmlClient().getZookeeperTimeout();
		}else if(LoaderXmlResources.SERVER.equalsIgnoreCase(instance.getServerType())){
			return (int)instance.getXmlServer().getZookeeperTimeout();
		}
		return 0;
	}
	
	/**
	 * 创建持久化节点
	 * @param node 节点
	 * @param data 节点对应的数据
	 */
	public void createPersistentNode(String node,String data){
		try {
			if(zookeeper.exists(node, true) == null){
				zookeeper.create(node,data.getBytes("UTF-8"), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			zookeeper.getChildren(node, true);
		} catch (KeeperException | InterruptedException | UnsupportedEncodingException e) {
			LoggerUtils.error(node+" 持久化节点创建失败!", e);
		}
	}
	
	/**
	 * 创建临时节点
	 * @param node 节点
	 * @param data 节点对应的数据
	 */
	public void createEphemeralNode(String node,String data){
		try {
			if(zookeeper.exists(node, true) == null){
				zookeeper.create(node,data.getBytes("UTF-8"), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			}
			zookeeper.getChildren(node, true);
		} catch (KeeperException | InterruptedException | UnsupportedEncodingException e) {
			LoggerUtils.error(node+" 临时节点创建失败!", e);
		}
	}
	
	/**
	 * 创建临时序列节点
	 * @param node 节点
	 * @param data 节点对应的数据
	 */
	public void createEphemeralSequentialNode(String node,String data){
		try {
			if(zookeeper.exists(node, true) == null){
				zookeeper.create(node,data.getBytes("UTF-8"), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			}
		} catch (KeeperException | InterruptedException | UnsupportedEncodingException e) {
			LoggerUtils.error(node+" 临时节点创建失败!", e);
		}
	}
	
	/**
	 * 创建临时序列节点
	 * @param node 节点
	 * @param data 节点对应的数据
	 */
	public void createEphemeralSequentialNode(String node,byte[] data){
		try {
			if(zookeeper.exists(node, true) == null){
				zookeeper.create(node,data, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			}
		} catch (KeeperException | InterruptedException e) {
			LoggerUtils.error(node+" 临时节点创建失败!", e);
		}
	}
	
	/**
	 * 删除单个持久化节点
	 * @param node
	 */
	public void deleteSinglePersistentNode(String node){
		try {
			if(zookeeper.exists(node, true) != null){
				zookeeper.delete(node, -1);
			}
		} catch (KeeperException | InterruptedException e) {
			LoggerUtils.error(node+" 持久化节点删除失败!", e);
		}
	}
	
	/**
	 * 删除当前节点及其所有子节点
	 * @param node
	 */
	public void deleteAllPersistentNode(String node){
		try {
			if(zookeeper.exists(node, true) != null){
				List<String> children = zookeeper.getChildren(node, true);
				if(children != null && !children.isEmpty()){
					for (String childNode : children) {
						String childNodePath = FileUtil.replaceSpritAndEnd(node)+childNode;
						deleteAllPersistentNode(childNodePath);
					}
				}
				deleteSinglePersistentNode(node);//删除单个持久化节点
			}
		} catch (KeeperException | InterruptedException e) {
			LoggerUtils.error(node+" 持久化节点删除失败!", e);
		}
	}
	
	/**
	 * 得到单例的zookeeper信息
	 * @return
	 * @throws IOException
	 */
	public static ZookeeperCofig getInstance() throws IOException{
		if(zookeeperCofig == null){
			zookeeperCofig = new ZookeeperCofig();
			ZookeeperEvent zookeeperEvent = new ZookeeperEventAdapter(zookeeperCofig);
			zookeeperCofig.zookeeperWatcher = new ZookeeperWatcher(zookeeperCofig,zookeeperEvent);
			zookeeperCofig.connectZookeeper();
			ExtensionLoader.setInstance(ZookeeperCofig.class, zookeeperCofig);
		}
		return zookeeperCofig;
	}

	public ZooKeeper getZookeeper() {
		return zookeeper;
	}
}
