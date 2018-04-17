package com.bjhy.fbackup.server.util;

import java.util.ArrayList;
import java.util.List;

import com.bjhy.fbackup.common.domain.Version;
import com.bjhy.fbackup.common.domain.XmlFbackup;
import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.common.util.CenterPropUtil;
import com.bjhy.fbackup.common.util.ConstantUtil;
import com.bjhy.fbackup.common.util.FileUtil;
import com.bjhy.fbackup.common.util.SeriUtil;
import com.bjhy.fbackup.common.zookeeper.curator.ZookeeperCuratorConfig;

/**
 * 该类用于处理center.properties配置文件中的信息
 * @author wubo
 */
public class ServerCenterUtil {
	
	/**
	 * 当前在线客户端
	 */
	private static final List<String> inlineClients = new ArrayList<String>();
	
	/**
	 * 是否同步当前客户端
	 * @param client
	 * @return
	 */
	public static Boolean isSyncClient(XmlFbackup client){
		getinlineClients();//得到当前在线所有客户端
		//如果当前在线客户端中存在 all ,则表示同步所具有客户端
		if(inlineClients.contains(ConstantUtil.SERVER_SYSN_ALL_CLIENT)){
			return true;
		}
		//得到客户端编号
		String clientNumber = client.getXmlClient().getClientNumber();
		clientNumber = clientNumber.toUpperCase().trim();
		//判断客户端是否存在与当前在线客户端中,存在则返回true,反之则返回false
		if(inlineClients.contains(clientNumber)){
			return true;
		}
		return false;
	}
	
	/**
	 * 得到当前在线所有客户端
	 * @return
	 */
	private static List<String> getinlineClients(){
		if(inlineClients.isEmpty()){
			List<String> propertyList = CenterPropUtil.getPropertyList("sync_client_number");
			
			if(propertyList == null || propertyList.isEmpty()){
				inlineClients.add(ConstantUtil.SERVER_SYSN_ALL_CLIENT);
			}else{
				for (String clientNumber : propertyList) {
					clientNumber = clientNumber.toUpperCase().trim();
					inlineClients.add(clientNumber);
				}
			}
		}
		return inlineClients;
	}
	
	/**
	 * 检测同步的版本
	 */
	public static void checkSyncVersion(){
		String staticsPicture = CenterPropUtil.getProperty("version_update_statics_picture",true);
		String databaseFile = CenterPropUtil.getProperty("version_update_database_file",true);
		
		if("true".equalsIgnoreCase(staticsPicture.trim())){
			Version version = getNodeVersion();
			long versionValue = FileUtil.getLongId();
			version.setVersionStaticsPicture(Long.toString(versionValue));
			setNodeVersion(version);
		}
		
		if("true".equalsIgnoreCase(databaseFile.trim())){
			Version version = getNodeVersion();
			long versionValue = FileUtil.getLongId();
			version.setVersionDatabaseFile(Long.toString(versionValue));
			setNodeVersion(version);
		}
		
		CenterPropUtil.setProperty("version_update_statics_picture", "false");
		CenterPropUtil.setProperty("version_update_database_file", "false");
	}
	
	/**
	 * 得到version
	 * @return
	 */
	private static Version getNodeVersion(){
		ZookeeperCuratorConfig zookeeperCuratorConfig = ExtensionLoader.getInstance(ZookeeperCuratorConfig.class);
		
		String versionNodePath = ZookeeperCuratorConfig.ROOT_NODE_VERSION;
		byte[] versionByte = zookeeperCuratorConfig.getNodeData(versionNodePath);
		Version version = SeriUtil.unserializeProtoStuffToObj(versionByte, Version.class);
		return version;
	}
	
	/**
	 * 设置version值
	 * @param version
	 */
	private static void setNodeVersion(Version version){
		ZookeeperCuratorConfig zookeeperCuratorConfig = ExtensionLoader.getInstance(ZookeeperCuratorConfig.class);
		
		String versionNodePath = ZookeeperCuratorConfig.ROOT_NODE_VERSION;
		byte[] versionByte = SeriUtil.serializeProtoStuffTobyteArray(version, Version.class);
		zookeeperCuratorConfig.setNodeData(versionNodePath, versionByte);
	}


}
