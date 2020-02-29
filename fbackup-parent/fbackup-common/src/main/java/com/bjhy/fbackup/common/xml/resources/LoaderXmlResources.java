package com.bjhy.fbackup.common.xml.resources;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.bjhy.fbackup.common.domain.DirectoryInfo;
import com.bjhy.fbackup.common.domain.XmlClient;
import com.bjhy.fbackup.common.domain.XmlFbackup;
import com.bjhy.fbackup.common.domain.XmlServer;
import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.common.util.ConstantUtil;
import com.bjhy.fbackup.common.util.FileUtil;
import com.bjhy.fbackup.common.util.LoggerUtils;
import com.bjhy.fbackup.common.util.NativeHostUtil;

/**
 * 加载xml资源
 * @author wubo
 *
 */
@SuppressWarnings("unchecked")
public class LoaderXmlResources {
	
	public static final String CLIENT = "client";
	public static final String SERVER = "server";
	private final String fbackupXml = "/config/fbackup.xml";
	
	/**
	 * 加载xml文件
	 */
	public XmlFbackup loadFileXml(){
		XmlFbackup xmlFbackup = new XmlFbackup();
		try {
			File dbFile = new File(System.getProperty("user.dir")+fbackupXml);
			SAXReader reader = new SAXReader();
			Document document = reader.read(dbFile);
			Element root = document.getRootElement();
			
			parseXmlFbackupElement(root, xmlFbackup);
			LoggerUtils.info("通过xml的形式加载所有数据源成功!!");
		} catch (DocumentException e) {
			LoggerUtils.error("通过xml的形式加载所有数据源失败!!",e);
		}
		XmlFbackup instance = ExtensionLoader.getInstance(XmlFbackup.class);
		
		if(instance != null){
			ExtensionLoader.remove(XmlFbackup.class);
		}
		ExtensionLoader.setInstance(XmlFbackup.class, xmlFbackup);//向扩展点容器中注册该实例
		return xmlFbackup;
	}
	
	/**
	 * 解析xmlFbackup的根配置元素
	 * @param parent
	 * @param xmlFbackup
	 */
	private void parseXmlFbackupElement(Element parent,XmlFbackup xmlFbackup){
		Element serverTypeElement = parent.element("server-type");
		Element serverIpElement = parent.element("server-ip");
		Element serverPortElement = parent.element("server-port");
		Element serverContextElement = parent.element("server-context");
		
		String serverType = serverTypeElement.getText();
		String serverIp = null;
		String serverPort = serverPortElement.getText();
		String serverContext = serverContextElement.getText();
		
		if(serverIpElement != null){
			String ip = serverIpElement.getText();
			if(org.apache.commons.lang3.StringUtils.isNoneBlank(ip)){
				serverIp = ip.trim();
			}
		}
		
		if(StringUtils.isEmpty(serverType)){
			LoggerUtils.error("server-type 标签不能空");
			return;
		}
		
		if(StringUtils.isEmpty(serverPort)){
			LoggerUtils.error("server-port 标签不能空");
			return;
		}
		
		if(StringUtils.isEmpty(serverContext)){
			LoggerUtils.error("server-Context 标签不能空");
			return;
		}
		
		xmlFbackup.setServerType(serverType);
		serverIp = StringUtils.isNotBlank(serverIp)?serverIp:NativeHostUtil.getHostAddress();
		String serverId = serverIp+"-"+FileUtil.getLongId();
		xmlFbackup.setServerIp(serverIp);
		xmlFbackup.setServerId(serverId);
		xmlFbackup.setServerPort(serverPort);
		xmlFbackup.setServerContext(serverContext);
		
		/**
		 * client客户端
		 */
		if(serverType.equalsIgnoreCase(CLIENT)){
			xmlFbackup.setNotifyStatus(ConstantUtil.NOTIFY_STATUS_CLIENT_NO_DATA);
			Element client = parent.element(CLIENT);
			XmlClient xmlClient = parseXmlClientElement(client);
			xmlFbackup.setXmlClient(xmlClient);
			
		/**
		 * server服务端
		 */
		}else if(serverType.equalsIgnoreCase(SERVER)){
			xmlFbackup.setNotifyStatus(ConstantUtil.NOTIFY_STATUS_SERVER_NO_DATA);
			Element server = parent.element(SERVER);
			XmlServer xmlServer = parseXmlServerElement(server);
			xmlFbackup.setXmlServer(xmlServer);
		}
	}
	
	/**
	 * 解析client元素
	 * @param parent
	 * @return
	 */
	private XmlClient parseXmlClientElement(Element parent){
		XmlClient xmlClient = new XmlClient();
		String zookeeperAddress = parent.element("zookeeper-address").getText();
		String zookeeperTimeout = parent.element("zookeeper-timeout").getText();
		xmlClient.setZookeeperAddress(zookeeperAddress);
		if(StringUtils.isNotEmpty(zookeeperTimeout)){
			xmlClient.setZookeeperTimeout(Long.parseLong(zookeeperTimeout));
		}
		
		String clientNumber = parent.element("client-number").getText();
		String clientName = parent.element("client-name").getText();
		xmlClient.setClientNumber(clientNumber);
		xmlClient.setClientName(clientName);
		
		Element readDirectoryListElement = parent.element("read-directory-list");
		List<Element> elements = readDirectoryListElement.elements("read-directory");
		
		for (Element element : elements) {
			String content = element.getText();
			content = FileUtil.replaceSpritAndEnd(content.trim());
			String directoryType = element.attribute("directory-type").getText().trim();

			String customField = null;
			Attribute attribute = element.attribute("custom-field");
			if(attribute != null && attribute.getText() != null) {
				customField = attribute.getText().trim();
			}
			
			DirectoryInfo directoryInfo = new DirectoryInfo(directoryType, content, customField);
			xmlClient.getDirectoryList().add(directoryInfo);
		}
		return xmlClient;
	}
	
	/**
	 * 解析server元素
	 * @param parent
	 * @return
	 */
	private XmlServer parseXmlServerElement(Element parent){
		XmlServer xmlServer = new XmlServer();
		String zookeeperAddress = parent.element("zookeeper-address").getText();
		String zookeeperTimeout = parent.element("zookeeper-timeout").getText();
		xmlServer.setZookeeperAddress(zookeeperAddress);
		if(StringUtils.isNotEmpty(zookeeperTimeout)){
			xmlServer.setZookeeperTimeout(Long.parseLong(zookeeperTimeout));
		}
		
		String serverNumber = parent.element("server-number").getText();
		String serverName = parent.element("server-name").getText();
		xmlServer.setServerNumber(serverNumber);
		xmlServer.setServerName(serverName);
		
		Element storeDirectoryListElement = parent.element("store-directory-list");
		List<Element> elements = storeDirectoryListElement.elements("store-directory");
		
		for (Element element : elements) {
			String content = element.getText();
			content = FileUtil.replaceSpritAndEnd(content.trim());
			String directoryType = element.attribute("directory-type").getText().trim();
			
			String customField = null;
			Attribute attribute = element.attribute("custom-field");
			if(attribute != null && attribute.getText() != null) {
				customField = attribute.getText().trim();
			}
			
			DirectoryInfo directoryInfo = new DirectoryInfo(directoryType, content, customField);
			xmlServer.getDirectoryList().add(directoryInfo);
		}
		return xmlServer;
	}
}
