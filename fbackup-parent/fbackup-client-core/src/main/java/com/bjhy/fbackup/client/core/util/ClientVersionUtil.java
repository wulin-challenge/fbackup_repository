package com.bjhy.fbackup.client.core.util;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.bjhy.fbackup.client.core.dao.TransferMappingEntityDao;
import com.bjhy.fbackup.client.core.domain.TransferMappingEntity;
import com.bjhy.fbackup.common.domain.Version;
import com.bjhy.fbackup.common.util.CenterPropUtil;
import com.bjhy.fbackup.common.util.ConstantUtil;
import com.bjhy.fbackup.common.util.LoggerUtils;
import com.bjhy.fbackup.common.util.SeriUtil;
import com.bjhy.fbackup.common.zookeeper.curator.ZookeeperCuratorConfig;

/**
 * 客户端版本信息工具类
 * @author wubo
 *
 */
public class ClientVersionUtil {
	
	/**
	 * 得到静态版本
	 * @return
	 */
	public static String getVersionStaticPicture(){
		String staticsPicture = CenterPropUtil.getProperty("version_statics_picture",true);
		if(StringUtils.isBlank(staticsPicture)){
			return "";
		}
		return staticsPicture;
	}
	
	/**
	 * 得到数据库备份版本
	 * @return
	 */
	public static String getVersionDatabaseFile(){
		String databaseFile = CenterPropUtil.getProperty("version_database_file",true);
		if(StringUtils.isBlank(databaseFile)){
			return "";
		}
		return databaseFile;
	}
	
	/**
	 * 处理版本
	 * @param path
	 * @param data
	 */
	public static void dealWithVersion(String path,byte[] data){
		if(path.startsWith(ZookeeperCuratorConfig.ROOT_NODE_VERSION)){
			Version version = SeriUtil.unserializeProtoStuffToObj(data, Version.class);
			
			if(!(version.getVersionStaticsPicture().equals(ClientVersionUtil.getVersionStaticPicture()))){
				updateData(ConstantUtil.DIRECTORY_TYPE_PICTURE);
				CenterPropUtil.setProperty("version_statics_picture", version.getVersionStaticsPicture());
			}
			
			if(!(version.getVersionDatabaseFile().equals(ClientVersionUtil.getVersionDatabaseFile()))){
				updateData(ConstantUtil.DIRECTORY_TYPE_DATABASE);
				CenterPropUtil.setProperty("version_database_file", version.getVersionDatabaseFile());
			}
		}
	}

	/**
	 * 更新数据库
	 * @param directoryType 目录类型
	 */
	private static void updateData(String directoryType){
		try {
			
			String sql = "select m.* from base_file_transfer f left join base_transfer_mapping m on f.id = m.fileTransferId where f.directoryType =:directoryType";
			TransferMappingEntityDao<String, TransferMappingEntity> mappingDao = InstanceUtil.getTransferMappingEntityDao();
			
			Map<String,Object> conditions = new HashMap<String,Object>();
			conditions.put("directoryType", directoryType);
			
			List<TransferMappingEntity> findListByCondition = mappingDao.findListByCondition(sql, conditions);
			for (TransferMappingEntity mapping : findListByCondition) {
				mappingDao.deleteEntity(mapping);
			}
		} catch (SQLException e) {
			LoggerUtils.error("更新数据失败",e);
		}
	}
}
