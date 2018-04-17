package com.bjhy.fbackup.common.test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bjhy.fbackup.common.store.derby.DerbyDatabase;
import com.bjhy.fbackup.common.store.derby.InitRepository;
import com.bjhy.fbackup.common.test.dao.FileTransferEntityDao;
import com.bjhy.fbackup.common.test.dao.impl.FileTransferEntityDaoImpl;
import com.bjhy.fbackup.common.test.domain.FileTransferEntityTest;

public class TestFileTransferEntityDao {
	
	public static void main(String[] args) throws SQLException {
		DerbyDatabase.initDerby("test_derby");
		InitRepository.loadEntityTable();
		FileTransferEntityDao<String,FileTransferEntityTest> fileTransferEntityDao = new FileTransferEntityDaoImpl();
		

		testSql(fileTransferEntityDao);
	}
	
	/**
	 * 测试sql
	 * @param fileTransferEntityDao
	 * @throws SQLException 
	 */
	public static void testSql(FileTransferEntityDao<String,FileTransferEntityTest> fileTransferEntityDao) throws SQLException{
		
//		List<FileTransferEntity> findEntityListBySql = fileTransferEntityDao.findEntityListBySql("select * from base_file_transfer offset 1000 rows fetch next 10000 rows only");
//		
//		for (FileTransferEntity fileTransferEntity : findEntityListBySql) {
//			System.out.println(fileTransferEntity.getId()+" , "+fileTransferEntity.getClientName()+" , "+fileTransferEntity.getClientNumber());
//			fileTransferEntityDao.deleteEntity(fileTransferEntity);;
//		}
//		
		
		fileTransferEntityDao.executeSql("delete from base_file_transfer");
		Integer findDecimalNumberBySql = fileTransferEntityDao.findDecimalNumberBySql("select count(1) from base_file_transfer");
		
		System.out.println(findDecimalNumberBySql);
	}
	
	/**
	 * 测试性能
	 */
	public static void test(){
//		saveEntity(fileTransferEntityDao);
//		updateEntity(fileTransferEntityDao);
//		List<FileTransferEntity> findAll = fileTransferEntityDao.findAll();
//		for (FileTransferEntity fileTransferEntity : findAll) {
//			System.out.println(fileTransferEntity.getId()+" , "+fileTransferEntity.getClientName()+" , "+fileTransferEntity.getClientNumber());
//		}
		
//		Map<String,Object> params = new HashMap<String,Object>();
//		params.put("id", "11111");
//		
//		Integer findDecimalNumber = fileTransferEntityDao.findDecimalNumber(params);
//		System.out.println(findDecimalNumber);
////		
//		
//		List<FileTransferEntity> findListByIds = fileTransferEntityDao.findListByIds(Arrays.asList("111111","111112","1111123","1111124"));
//		
//		for (FileTransferEntity fileTransferEntity : findListByIds) {
////			System.out.println(fileTransferEntity.getId()+" , "+fileTransferEntity.getClientName()+" , "+fileTransferEntity.getClientNumber());
////			fileTransferEntityDao.deleteEntity(fileTransferEntity);;
//		}
//		System.out.println();
	}
	
	//保存
	
	private static void saveEntity(FileTransferEntityDao<String,FileTransferEntityTest> fileTransferEntityDao){
		FileTransferEntityTest FileTransferEntity = new FileTransferEntityTest();
		FileTransferEntity.setId("111112");
		FileTransferEntity.setClientName("ClientName");
		FileTransferEntity.setClientNumber("clientNumber");
		FileTransferEntity.setDirectoryType("directoryType");
		FileTransferEntity.setFileName("fileName");
		FileTransferEntity.setFilePath("filePath");
		FileTransferEntity.setFileStatus("fileStatus");
		FileTransferEntity.setFileTransferStatus("fileTransferStatus");
		FileTransferEntity.setFileTransferTime(new Date());
		FileTransferEntity.setServerIp("serverIp");
		FileTransferEntity.setServerType("serverType");
		
		try {
			fileTransferEntityDao.saveEntity(FileTransferEntity);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//保存
	
		private static void saveEntity(FileTransferEntityDao<String,FileTransferEntityTest> fileTransferEntityDao,int i){
			FileTransferEntityTest FileTransferEntity = new FileTransferEntityTest();
			FileTransferEntity.setId("111112"+i);
			FileTransferEntity.setClientName("ClientName"+i);
			FileTransferEntity.setClientNumber("clientNumber"+i);
			FileTransferEntity.setDirectoryType("directoryType"+i);
			FileTransferEntity.setFileName("fileName"+i);
			FileTransferEntity.setFilePath("filePath"+i);
			FileTransferEntity.setFileStatus("fileStatus"+i);
			FileTransferEntity.setFileTransferStatus("fileTransferStatus"+i);
			FileTransferEntity.setFileTransferTime(new Date());
			FileTransferEntity.setServerIp("serverIp"+i);
			FileTransferEntity.setServerType("serverType"+i);
			
			try {
				fileTransferEntityDao.saveEntity(FileTransferEntity);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	
	private static void updateEntity(FileTransferEntityDao<String,FileTransferEntityTest> fileTransferEntityDao){
		FileTransferEntityTest FileTransferEntity = new FileTransferEntityTest();
		FileTransferEntity.setId("111112");
		FileTransferEntity.setClientName("ClientName2");
		FileTransferEntity.setClientNumber("clientNumber2");
		FileTransferEntity.setDirectoryType("directoryType2");
		FileTransferEntity.setFileName("fileName2");
		FileTransferEntity.setFilePath("filePath2");
		FileTransferEntity.setFileStatus("fileStatus2");
		FileTransferEntity.setFileTransferStatus("fileTransferStatus2");
		FileTransferEntity.setFileTransferTime(new Date());
		FileTransferEntity.setServerIp("serverIp2");
		FileTransferEntity.setServerType("serverType2");
		
		try {
			fileTransferEntityDao.updateEntity(FileTransferEntity);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

}
