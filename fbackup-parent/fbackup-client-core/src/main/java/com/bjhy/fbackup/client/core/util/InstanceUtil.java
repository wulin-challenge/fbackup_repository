package com.bjhy.fbackup.client.core.util;

import com.bjhy.fbackup.client.core.dao.FileTransferEntityDao;
import com.bjhy.fbackup.client.core.dao.TransferMappingEntityDao;
import com.bjhy.fbackup.client.core.dao.impl.FileTransferEntityDaoImpl;
import com.bjhy.fbackup.client.core.dao.impl.TransferMappingEntityDaoImpl;
import com.bjhy.fbackup.client.core.domain.FileTransferEntity;
import com.bjhy.fbackup.client.core.domain.TransferMappingEntity;
import com.bjhy.fbackup.common.extension.ExtensionLoader;

/**
 * 实力工具类
 * @author wubo
 */
public class InstanceUtil {
	
	@SuppressWarnings("unchecked")
	public static FileTransferEntityDao<String,FileTransferEntity> getFileTransferEntityDao(){
		FileTransferEntityDao<String,FileTransferEntity> instance = ExtensionLoader.getInstance(FileTransferEntityDao.class);
		
		if(instance == null){
			instance = new FileTransferEntityDaoImpl();
			ExtensionLoader.setInstance(FileTransferEntityDao.class,instance );
		}
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	public static TransferMappingEntityDao<String, TransferMappingEntity> getTransferMappingEntityDao(){
		TransferMappingEntityDao<String,TransferMappingEntity> instance = ExtensionLoader.getInstance(TransferMappingEntityDao.class);
		
		if(instance == null){
			instance = new TransferMappingEntityDaoImpl();
			ExtensionLoader.setInstance(TransferMappingEntityDao.class,instance );
		}
		return instance;
	}

}
