package com.bjhy.fbackup.client.base;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.bjhy.fbackup.client.dao.FileTransferEntityDao;
import com.bjhy.fbackup.client.domain.BaseInfoMedaiInfo;
import com.bjhy.fbackup.client.domain.DirectoryTypePicture;
import com.bjhy.fbackup.client.domain.FileTransferEntity;
import com.bjhy.fbackup.client.statics.LoaderYzDatasource;
import com.bjhy.fbackup.client.util.BaseInfoMedaiInfoUtil;
import com.bjhy.fbackup.client.util.InstanceUtil;
import com.bjhy.fbackup.common.annotation.FBackupListener;
import com.bjhy.fbackup.common.domain.XmlClient;
import com.bjhy.fbackup.common.domain.XmlFbackup;
import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.common.util.ConstantUtil;
import com.bjhy.fbackup.common.util.FileUtil;
import com.bjhy.fbackup.common.util.LoggerUtils;
import com.bjhy.fbackup.common.util.SeriUtil;
import com.bjhy.fbackup.common.zookeeper.curator.ZookeeperCuratorConfig;

@FBackupListener
public class BaseReadPictureType implements ReadPictureType{
	
	/**
	 * 资源对象
	 */
	private XmlFbackup xmlFbackup = ExtensionLoader.getInstance(XmlFbackup.class);
	private DirectoryTypePicture directoryTypePicture;
	
	/**
	 * 扫描任务(每次只开启一个线程其周期执行)
	 */
	private final ScheduledThreadPoolExecutor sconningTask = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);


	@Override
	public void dealWithPicture(String directoryType, List<String> pictureList) {
		Boolean isLoaderDataSource = initLoaderYzDataSource();//初始化加载狱政的数据源
		if(isLoaderDataSource){
			executeScanningTask();
			
		}else{
			LoggerUtils.error("狱政数据源加载失败!请检查狱政数据源的配置");
		}
	}
	
	/**
	 * 执行扫描任务
	 */
	private void executeScanningTask(){
		
		sconningTask.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					scanningMediaData();//扫描媒体数据
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 5, 15, TimeUnit.SECONDS);
	}
	
	
	
	/**
	 * 扫描媒体数据
	 */
	private void scanningMediaData(){
		int dataTotal = BaseInfoMedaiInfoUtil.getDataTotalNumber();
		int pageNumber = BaseInfoMedaiInfoUtil.getPageNumber(dataTotal, BaseInfoMedaiInfoUtil.currentPageNumber);
		for (int i = 1; i <= pageNumber; i++) {
			List<BaseInfoMedaiInfo> pageListData = BaseInfoMedaiInfoUtil.getPageListData(i);
			for (BaseInfoMedaiInfo baseInfoMedaiInfo : pageListData) {
				scanningMediaData(baseInfoMedaiInfo);
			}
		}
	}
	
	/**
	 * 扫描媒体数据
	 * @param baseInfoMedaiInfo
	 */
	private void scanningMediaData(BaseInfoMedaiInfo baseInfoMedaiInfo){
		checkAndStoreFile(XmlClient.DIRECTORY_TYPE_PICTURE, baseInfoMedaiInfo.getMediaInfoMturl());
		Boolean hasTransferData = hasTransferData();
		if(hasTransferData){
			updateCurrentNode();
		}
	}
	
	/**
	 * 初始化加载狱政的数据源
	 */
	private Boolean initLoaderYzDataSource(){
		directoryTypePicture = ClientStorePath.getPictureByStaticType(ConstantUtil.STATIC_TYPE);
		if(directoryTypePicture != null){
			LoaderYzDatasource loaderYzDatasource = new LoaderYzDatasource();
			loaderYzDatasource.loaderYzJdbcTemplate(directoryTypePicture);
			ExtensionLoader.setInstance(LoaderYzDatasource.class, loaderYzDatasource);
			return true;
		}
		return false;
	}
	
	/**
	 * 检查并存储文件
	 * @param fileType
	 * @param oneFile
	 */
	private void checkAndStoreFile(String fileType,String relativeFilePath){
		FileTransferEntityDao<String, FileTransferEntity> fileTransferEntityDao = InstanceUtil.getFileTransferEntityDao();
		
		if(StringUtils.isNotBlank(relativeFilePath)){
			String checkSql = "select count(1) from base_file_transfer where  relativeFilePath=:relativeFilePath";
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("relativeFilePath", relativeFilePath);
			
			try {
				Integer hasData = fileTransferEntityDao.findDecimalNumberBySql(checkSql, params);
				if(hasData == 0){
					FileTransferEntity fileTransferEntity = getFileTransferEntity(fileType, relativeFilePath);
					fileTransferEntityDao.saveEntity(fileTransferEntity);
				}
			} catch (SQLException e) {
				LoggerUtils.error("查询时出错!",e);
			}
		}
	}
	
	/**
	 * 有数据?
	 * @return
	 */
	private Boolean hasTransferData(){
		
		String sql = "select count(1) from base_file_transfer  f left join "
				+" base_transfer_mapping m "
				+" on f.id = m.fileTransferId where m.serverNumber is null";
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
		XmlFbackup xmlFbackup = SeriUtil.unserializeProtoStuffToObj(nodeData, XmlFbackup.class);
		xmlFbackup.setNotifyStatus(ConstantUtil.NOTIFY_STATUS_CLIENT_HAS_DATA);
		byte[] xmlFbackupData = SeriUtil.serializeProtoStuffTobyteArray(xmlFbackup, XmlFbackup.class);
		instance.setNodeData(currentNode, xmlFbackupData);
	}
	
	/**
	 * 得到文件传输实体
	 * @param fileType
	 * @param relativeFilePath
	 * @return
	 */
	private FileTransferEntity getFileTransferEntity(String fileType,String relativeFilePath){
		String absoluteFilePath = FileUtil.replaceSpritAndEnd(directoryTypePicture.getStaticServiceUrl())+relativeFilePath;
		String name = "";
		
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

}
