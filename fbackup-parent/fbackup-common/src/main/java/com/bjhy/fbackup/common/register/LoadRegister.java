package com.bjhy.fbackup.common.register;

import java.io.IOException;

import com.bjhy.fbackup.common.domain.XmlFbackup;
import com.bjhy.fbackup.common.util.FileUtil;
import com.bjhy.fbackup.common.util.LoggerUtils;
import com.bjhy.fbackup.common.util.SeriUtil;
import com.bjhy.fbackup.common.xml.resources.LoaderXmlResources;
import com.bjhy.fbackup.common.zookeeper.ZookeeperCofig;

/**
 * 加载配置文件并向注册中心注册信息 : 是用 LoadResourceRegisterCurator 替代
 * @author wubo
 */
@Deprecated
public class LoadRegister {
	
	private ZookeeperCofig zookeeperCofig;
	
	private XmlFbackup loadFileXml;
	
	/**
	 * 加载xml文件并注册zookeeper
	 */
	public void loadXmlRegisterZookeeper(){
		LoaderXmlResources loaderXmlResources = new LoaderXmlResources();
		loadFileXml = loaderXmlResources.loadFileXml();
		try {
			zookeeperCofig = ZookeeperCofig.getInstance();
			registerInfoToZookeeper();//注册信息向zookeeper
		} catch (IOException e) {
			LoggerUtils.error("zookeeper连接失败", e);
		}
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
		byte[] loadFileXmlByte = SeriUtil.serializeProtoStuffTobyteArray(loadFileXml, XmlFbackup.class);
		zookeeperCofig.createEphemeralSequentialNode(ZookeeperCofig.ROOT_NODE_CLIENT_IP, loadFileXmlByte);
	}
	
	/**
	 * 注册服务端的信息
	 */
	private void registerServerInfo(){
		byte[] loadFileXmlByte = SeriUtil.serializeProtoStuffTobyteArray(loadFileXml, XmlFbackup.class);
		zookeeperCofig.createEphemeralSequentialNode(ZookeeperCofig.ROOT_NODE_SERVER_IP, loadFileXmlByte);
	}
	
}
