package com.bjhy.fbackup.client.base;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.bjhy.fbackup.client.dao.FileTransferEntityDao;
import com.bjhy.fbackup.client.domain.FileTransferEntity;
import com.bjhy.fbackup.client.util.InstanceUtil;
import com.bjhy.fbackup.common.domain.XmlFbackup;
import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.common.util.ConstantUtil;
import com.bjhy.fbackup.common.util.FileUtil;
import com.bjhy.fbackup.common.util.LoggerUtils;
import com.bjhy.fbackup.common.util.SeriUtil;
import com.bjhy.fbackup.common.zookeeper.curator.ZookeeperCuratorConfig;

/**
 * database处理类
 * @author wubo
 *
 */
public class BaseDatabaseDealWith {
	
	private final XmlFbackup xmlFbackup = ExtensionLoader.getInstance(XmlFbackup.class);
	
	private BaseDatabaseDealWith(){}
	/**
	 * 检查并存储文件
	 * @param fileType
	 * @param oneFile
	 */
	public void checkAndStoreFile(String fileType,File oneFile,String relativeFilePath){
		FileTransferEntityDao<String, FileTransferEntity> fileTransferEntityDao = InstanceUtil.getFileTransferEntityDao();
		
		if(StringUtils.isNotBlank(relativeFilePath)){
			String checkSql = "select count(1) from base_file_transfer where  relativeFilePath=:relativeFilePath";
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("relativeFilePath", relativeFilePath);
			
			try {
				Integer hasData = fileTransferEntityDao.findDecimalNumberBySql(checkSql, params);
				if(hasData == 0){
					FileTransferEntity fileTransferEntity = getFileTransferEntity(fileType, oneFile, relativeFilePath);
					fileTransferEntityDao.saveEntity(fileTransferEntity);
				}
			} catch (SQLException e) {
				LoggerUtils.error("查询时出错!",e);
			}
		}
	}
	
	/**
	 * 传输的文件是否存在
	 * @param directoryType 文件目录类型
	 * @param relativeFilePath 相对文件路径
	 * @return
	 */
	public Boolean transferFileExist(String directoryType,String relativeFilePath){
		FileTransferEntityDao<String, FileTransferEntity> fileTransferEntityDao = InstanceUtil.getFileTransferEntityDao();
		
		if(StringUtils.isNotBlank(relativeFilePath)){
			String checkSql = "select count(1) from base_file_transfer where  relativeFilePath=:relativeFilePath and directoryType=:directoryType";
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("relativeFilePath", relativeFilePath);
			params.put("directoryType", directoryType);
			
			try {
				int hasData = fileTransferEntityDao.findDecimalNumberBySql(checkSql, params);
				
				if(hasData == 0){
					return true;
				}
			} catch (SQLException e) {
				LoggerUtils.error("查询时出错!",e);
			}
		}
		return false;
	}
	
	/**
	 * 有数据?
	 * @return
	 */
	public Boolean hasTransferData(){
		
		String sql = "select count(1) from base_file_transfer f left join base_transfer_mapping m on f.id=m.fileTransferId"
				+ " where m.fileTransferId is null";
		FileTransferEntityDao<String, FileTransferEntity> fileTransferEntityDao = InstanceUtil.getFileTransferEntityDao();
		try {
			Integer findDecimalNumberBySql = fileTransferEntityDao.findDecimalNumberBySql(sql);
			if(findDecimalNumberBySql>0){
				return true;
			}
		} catch (SQLException e) {
			LoggerUtils.warn("没数据要传输!");
		}
		return false;
	}
	
	/**
	 * 更新当前节点
	 */
	public void updateCurrentNode(){
		String currentNode = ZookeeperCuratorConfig.CurrentNode;
		ZookeeperCuratorConfig instance = ExtensionLoader.getInstance(ZookeeperCuratorConfig.class);
		byte[] nodeData = instance.getNodeData(currentNode);
		if(nodeData != null){
			XmlFbackup xmlFbackup = SeriUtil.unserializeProtoStuffToObj(nodeData, XmlFbackup.class);
			xmlFbackup.setNotifyStatus(ConstantUtil.NOTIFY_STATUS_CLIENT_HAS_DATA);
			byte[] xmlFbackupData = SeriUtil.serializeProtoStuffTobyteArray(xmlFbackup, XmlFbackup.class);
			instance.setNodeData(currentNode, xmlFbackupData);
//			LoggerUtils.info("当前节点有数据,通知zookeeper成功!");
		}else{
			LoggerUtils.info("zookeeper 断线,节点更新失败!");
		}
		
	}
	
	/**
	 * 得到文件传输实体
	 * @param fileType
	 * @param oneFile
	 * @param relativeFilePath
	 * @return
	 */
	public FileTransferEntity getFileTransferEntity(String fileType,File oneFile,String relativeFilePath){
		String absoluteFilePath = oneFile.getAbsolutePath();
		String name = oneFile.getName();
		
		FileTransferEntity fileTransfer = new FileTransferEntity();
		fileTransfer.setClientName(xmlFbackup.getXmlClient().getClientName());
		fileTransfer.setClientNumber(xmlFbackup.getXmlClient().getClientNumber());
		fileTransfer.setDirectoryType(fileType);
		fileTransfer.setFileName(name);
		fileTransfer.setAbsoluteFilePath(FileUtil.replaceSprit(absoluteFilePath));
		fileTransfer.setRelativeFilePath(FileUtil.replaceSprit(relativeFilePath));
		fileTransfer.setFileStatus(ConstantUtil.FILE_STATUS_CREATE);
		fileTransfer.setId(FileUtil.getUUID());
		fileTransfer.setServerIp(xmlFbackup.getServerIp());
		fileTransfer.setServerType(xmlFbackup.getServerType());
		return fileTransfer;
	}

	/**
	 * 得到 BaseDatabaseDealWith 实例
	 * @return
	 */
	public static BaseDatabaseDealWith getInstance(){
		BaseDatabaseDealWith instance = ExtensionLoader.getInstance(BaseDatabaseDealWith.class);
		if(instance == null){
			instance = new BaseDatabaseDealWith();
			ExtensionLoader.setInstance(BaseDatabaseDealWith.class, instance);
		}
		return instance;
	}
}
