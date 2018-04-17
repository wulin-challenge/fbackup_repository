package com.bjhy.fbackup.client.listener;

import java.io.File;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bjhy.fbackup.client.base.BaseDatabaseDealWith;
import com.bjhy.fbackup.client.base.ClientStorePath;
import com.bjhy.fbackup.client.dao.FileTransferEntityDao;
import com.bjhy.fbackup.client.dao.TransferMappingEntityDao;
import com.bjhy.fbackup.client.domain.FileTransferEntity;
import com.bjhy.fbackup.client.domain.TransferMappingEntity;
import com.bjhy.fbackup.client.util.InstanceUtil;
import com.bjhy.fbackup.common.annotation.FBackupListener;
import com.bjhy.fbackup.common.domain.XmlClient;
import com.bjhy.fbackup.common.file.watcher.TransferDealWith;
import com.bjhy.fbackup.common.util.ConstantUtil;
import com.bjhy.fbackup.common.util.FileUtil;
import com.bjhy.fbackup.common.util.LoggerUtils;

@FBackupListener
public class FileWatcherClientListener implements TransferDealWith{
	private FileTransferEntityDao<String, FileTransferEntity> transferDao = InstanceUtil.getFileTransferEntityDao();
	private TransferMappingEntityDao<String, TransferMappingEntity> mappingDao = InstanceUtil.getTransferMappingEntityDao();

	@Override
	public void fileCreateEvent(WatchKey watckKey, WatchEvent<?> event) {
		fileEventDealWith(watckKey, event,"fileCreateEvent");//文件事件处理
	}

	@Override
	public void fileModifyEvent(WatchKey watckKey, WatchEvent<?> event) {
		fileEventDealWith(watckKey, event,"fileModifyEvent");//文件事件处理
		
	}

	@Override
	public void fileDeleteEvent(WatchKey watckKey, WatchEvent<?> event) {
		String filePath = FileUtil.getFilePathOfFileWatcher(watckKey, event);
		String relativeFilePath = ClientStorePath.getDatabaseRelativePath(filePath);
		try {
			final BaseDatabaseDealWith database = BaseDatabaseDealWith.getInstance();
			Boolean exist = database.transferFileExist(XmlClient.DIRECTORY_TYPE_DATABASE, relativeFilePath);
			if(exist){
				Map<String,Object> conditions = new HashMap<String,Object>();
				conditions.put("relativeFilePath", relativeFilePath);
				List<FileTransferEntity> TransferEntityList = transferDao.findListByCondition(conditions);
				for (FileTransferEntity transferEntity : TransferEntityList) {
					transferDao.deleteEntity(transferEntity);
				}
			}
		} catch (Exception e) {
			LoggerUtils.error("fileDeleteEvent 出错了",e);
		}
	}
	
	/**
	 * 文件事件处理
	 * @param watckKey
	 * @param event
	 */
	private void fileEventDealWith(WatchKey watckKey, WatchEvent<?> event,String listenerType) {
		String filePath = FileUtil.getFilePathOfFileWatcher(watckKey, event);
		File oneFile = new File(filePath);
		if(!oneFile.exists() || oneFile.isDirectory()){
			return;
		}
		String relativeFilePath = ClientStorePath.getDatabaseRelativePath(filePath);
		try {
			final BaseDatabaseDealWith database = BaseDatabaseDealWith.getInstance();
			Boolean exist = database.transferFileExist(XmlClient.DIRECTORY_TYPE_DATABASE, relativeFilePath);
			if(exist){
				Map<String,Object> conditions = new HashMap<String,Object>();
				conditions.put("relativeFilePath", relativeFilePath);
				List<FileTransferEntity> TransferEntityList = transferDao.findListByCondition(conditions);
				for (FileTransferEntity transferEntity : TransferEntityList) {
					
					Map<String,Object> mappingCondition = new HashMap<String,Object>();
					mappingCondition.put("fileTransferId", transferEntity.getId());
					List<TransferMappingEntity> findListByCondition = mappingDao.findListByCondition(mappingCondition);
					for (TransferMappingEntity mapping : findListByCondition) {
						mappingDao.deleteEntity(mapping);
					}
				}
			}else{
				
				FileTransferEntity transferEntity = database.getFileTransferEntity(XmlClient.DIRECTORY_TYPE_DATABASE, oneFile, relativeFilePath);
				transferDao.saveOrUpdateEntity(transferEntity);
			}
			database.updateCurrentNode();
		} catch (Exception e) {
			LoggerUtils.error(listenerType+" /出错了",e);
		}
	}

}
