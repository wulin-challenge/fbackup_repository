package com.bjhy.fbackup.client.base;

import java.util.ArrayList;
import java.util.List;

import com.bjhy.fbackup.common.domain.XmlClient;
import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.common.util.ListenerUtil;
import com.bjhy.fbackup.common.util.LoggerUtils;

/**
 * client的核心类
 * @author wubo
 */
public class BaseClientCore {
	
	private static final List<Class<? extends ReadDatabaseType>> readDatabaseTypeList = ListenerUtil.getListenerClass(ReadDatabaseType.class);
	
	private static final List<Class<? extends ReadPictureType>> readPictureTypeList = ListenerUtil.getListenerClass(ReadPictureType.class);
	
	
	/**
	 * picture : 指的是静态资源
	 * database : 指的是数据库备份
	 * 
	 * 执行监听
	 */
	public void pictureAndDatabaseListener(){
		pictureListener();//图片监听
		databaseListener();//数据库文件监听
	}
	
	/**
	 * 图片监听
	 */
	private void pictureListener(){
		List<ReadPictureType> readPictureTypeInstances = readPictureTypeInstances();
		for (ReadPictureType readPictureType : readPictureTypeInstances) {
			List<String> pictureList = ClientStorePath.getPictureList();
			readPictureType.dealWithPicture(XmlClient.DIRECTORY_TYPE_PICTURE,pictureList);
		}
	}
	
	/**
	 * 数据库文件监听
	 */
	private void databaseListener(){
		List<ReadDatabaseType> readDatabaseTypeInstances = readDatabaseTypeInstances();
		for (ReadDatabaseType readDatabaseType : readDatabaseTypeInstances) {
			List<String> databaseList = ClientStorePath.getDatabaseList();
			readDatabaseType.dealWithDatabase(XmlClient.DIRECTORY_TYPE_DATABASE,databaseList);
		}
	}

	/**
	 * 得到所有 实现 ReadDatabaseType 接口的实例
	 * @return
	 */
	private List<ReadDatabaseType> readDatabaseTypeInstances(){
		List<ReadDatabaseType> readDatabaseTypeInstances = new ArrayList<ReadDatabaseType>();
		for (Class<? extends ReadDatabaseType> clazz : readDatabaseTypeList) {
			ReadDatabaseType instance = ExtensionLoader.getInstance(clazz);
			if(instance == null){
				try {
					instance = clazz.newInstance();
					ExtensionLoader.setInterfaceInstance(clazz, instance);
				} catch (InstantiationException e) {
					LoggerUtils.error("InstantiationException 异常", e);
				} catch (IllegalAccessException e) {
					LoggerUtils.error("IllegalAccessException 异常", e);
				}
			}
			readDatabaseTypeInstances.add(instance);
		}
		return readDatabaseTypeInstances;
	}
	
	/**
	 * 得到所有 实现 ReadPictureType 接口的实例
	 * @return
	 */
	private List<ReadPictureType> readPictureTypeInstances(){
		List<ReadPictureType> readPictureTypeInstances = new ArrayList<ReadPictureType>();
		for (Class<? extends ReadPictureType> clazz : readPictureTypeList) {
			ReadPictureType instance = ExtensionLoader.getInstance(clazz);
			if(instance == null){
				try {
					instance = clazz.newInstance();
					ExtensionLoader.setInterfaceInstance(clazz, instance);
				} catch (InstantiationException e) {
					LoggerUtils.error("InstantiationException 异常", e);
				} catch (IllegalAccessException e) {
					LoggerUtils.error("IllegalAccessException 异常", e);
				}
			}
			readPictureTypeInstances.add(instance);
		}
		return readPictureTypeInstances;
	}
	
}
