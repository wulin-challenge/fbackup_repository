package com.bjhy.fbackup.common.register;

import org.apache.zookeeper.CreateMode;

import com.bjhy.fbackup.common.domain.Version;
import com.bjhy.fbackup.common.domain.XmlFbackup;
import com.bjhy.fbackup.common.util.FileUtil;
import com.bjhy.fbackup.common.util.SeriUtil;
import com.bjhy.fbackup.common.xml.resources.LoaderXmlResources;
import com.bjhy.fbackup.common.zookeeper.curator.ZookeeperCuratorConfig;

/**
 * 加载配置文件并向注册中心注册信息
 * @author wubo
 */
public class LoadResourceRegisterCurator {
	
	private ZookeeperCuratorConfig zookeeperCuratorConfig;
	
	private XmlFbackup loadFileXml;
	
	/**
	 * 加载xml文件并注册zookeeper
	 */
	public void loadXmlRegisterZookeeper(){
		LoaderXmlResources loaderXmlResources = new LoaderXmlResources();
		loadFileXml = loaderXmlResources.loadFileXml();
		zookeeperCuratorConfig = ZookeeperCuratorConfig.getInstanceAndStartZookeeper();
		registerInfoToZookeeper();//注册信息向zookeeper
	}
	
	/**
	 * 注册信息向zookeeper
	 */
	private void registerInfoToZookeeper(){
		String serverType = loadFileXml.getServerType();
		
		//client
		if(LoaderXmlResources.CLIENT.equalsIgnoreCase(serverType)){
			registerClientInfo();//注册客户端的信息
			
		//server
		}else if(LoaderXmlResources.SERVER.equalsIgnoreCase(serverType)){
			registerServerInfo();//注册服务端的信息
		}
	}
	
	/**
	 * 注册客户端的信息
	 */
	private void registerClientInfo(){
		createVersionNodeAndeSetData();//创建版本节点并设置值
		createNodeAndSetData(ZookeeperCuratorConfig.ROOT_NODE_CLIENT);
	}
	
	/**
	 * 注册服务端的信息
	 */
	private void registerServerInfo(){
		createVersionNodeAndeSetData();//创建版本节点并设置值
		createNodeAndSetData(ZookeeperCuratorConfig.ROOT_NODE_SERVER);
	}
	
	/**
	 * 创建节点并设置节点数据
	 * @param rootPath 根路径
	 */
	private void createNodeAndSetData(String rootPath){
		byte[] loadFileXmlByte = SeriUtil.serializeProtoStuffTobyteArray(loadFileXml, XmlFbackup.class);
		String serverIp = loadFileXml.getServerId();
		String ipPath = FileUtil.replaceSpritAndEnd(rootPath)+serverIp;
		ZookeeperCuratorConfig.CurrentNode = ipPath;//为当前节点设置节点值
		zookeeperCuratorConfig.createNode(ipPath, CreateMode.EPHEMERAL);
		zookeeperCuratorConfig.setNodeData(ipPath, loadFileXmlByte);
	}
	
	/**
	 * 创建版本节点并设置值
	 */
	private void createVersionNodeAndeSetData(){
		String versionNodePath = ZookeeperCuratorConfig.ROOT_NODE_VERSION;
		Boolean exists = zookeeperCuratorConfig.existsNode(versionNodePath);
		if(!exists){
			zookeeperCuratorConfig.createNode(versionNodePath, CreateMode.PERSISTENT);
			long versionValue = FileUtil.getLongId();
			Version version = new Version();
			version.setVersionDatabaseFile(Long.toString(versionValue));
			version.setVersionStaticsPicture(Long.toString(versionValue));
			byte[] versionByte = SeriUtil.serializeProtoStuffTobyteArray(version, Version.class);
			zookeeperCuratorConfig.setNodeData(versionNodePath, versionByte);
		}
	}
}
