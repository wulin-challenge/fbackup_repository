package com.bjhy.fbackup.client.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bjhy.fbackup.client.dao.FileTransferEntityDao;
import com.bjhy.fbackup.client.dao.TransferMappingEntityDao;
import com.bjhy.fbackup.client.domain.FileTransferEntity;
import com.bjhy.fbackup.client.domain.TransferMappingEntity;
import com.bjhy.fbackup.client.util.InstanceUtil;
import com.bjhy.fbackup.common.domain.ClientFileTransfer;
import com.bjhy.fbackup.common.domain.DerbyPage;
import com.bjhy.fbackup.common.util.ConstantUtil;
import com.bjhy.fbackup.common.util.DerbyPageUtil;
import com.bjhy.fbackup.common.util.FileUtil;
import com.bjhy.fbackup.common.util.HttpClientUtil;
import com.bjhy.fbackup.common.util.LoggerUtils;
import com.bjhy.fbackup.common.util.RepositorySqlUtil;

/**
 * 客户端控制层
 * @author wubo
 */

@Controller
@RequestMapping("client")
public class ClientController {
	
	//传输锁
	private final Object transferLock = new Object();
	
	/**
	 * 得到客户端所有文件
	 * @return
	 */
	@RequestMapping("getClientAllFiles")
	public @ResponseBody List<ClientFileTransfer> getClientAllFiles(String serverNumber){
		String sql = "select f.* from base_file_transfer  f left join "
				   +" (select m1.* from base_transfer_mapping m1 where m1.serverNumber=:serverNumber) m "
				   +" on f.id = m.fileTransferId where m.serverNumber is null ";
	
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("serverNumber",serverNumber);
		FileTransferEntityDao<String, FileTransferEntity> fileTransferEntityDao = InstanceUtil.getFileTransferEntityDao();
		List<Map<String, Object>> ListData = fileTransferEntityDao.findMapListBySql(sql, params);
		
		List<ClientFileTransfer> clientFileTransferList = RepositorySqlUtil.listMaptoListEntity(ClientFileTransfer.class, ListData);
		return clientFileTransferList;
	}
	
	/**
	 * 分页得到所有文件
	 * @param currentPage 当前页
	 * @param perPageTotal 每页的数量
	 * @return
	 */
	@RequestMapping("getPageclientFiles")
	public @ResponseBody List<ClientFileTransfer> getPageclientFiles(int currentPage,int perPageTotal,String serverNumber){
		
		int skipNumber = DerbyPageUtil.getSkipNumber(currentPage, perPageTotal);
		String pageSql = "select f.* from base_file_transfer  f left join "
					   +" (select m1.* from base_transfer_mapping m1 where m1.serverNumber=:serverNumber) m "
					   +" on f.id = m.fileTransferId where m.serverNumber is null OFFSET "+skipNumber+" ROWS FETCH NEXT "+perPageTotal+" ROWS ONLY";
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("serverNumber", serverNumber);
		
		FileTransferEntityDao<String, FileTransferEntity> fileTransferEntityDao = InstanceUtil.getFileTransferEntityDao();
		List<Map<String, Object>> ListData = fileTransferEntityDao.findMapListBySql(pageSql, params);
		
		//将日期转为字符串格式
		for (Map<String, Object> map : ListData) {
			Set<Entry<String, Object>> entrySet = map.entrySet();
			for (Entry<String, Object> entry : entrySet) {
				Object value = entry.getValue();
				if(value instanceof Date){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					value = sdf.format(value);
					map.put(entry.getKey(), value);
				}
			}
		}
		List<ClientFileTransfer> clientFileTransferList = RepositorySqlUtil.listMaptoListEntity(ClientFileTransfer.class, ListData);
		return clientFileTransferList;
	}
	
	/**
	 * 得到客户端分页实体
	 * @return
	 */
	@RequestMapping("getDerbyPage")
	public @ResponseBody DerbyPage getDerbyPage(String serverNumber){
		DerbyPage derbyPage = new DerbyPage();
		
		FileTransferEntityDao<String, FileTransferEntity> fileTransferEntityDao = InstanceUtil.getFileTransferEntityDao();
		String totalDataSql = "select count(1) from base_file_transfer  f left join "
							+" (select m1.* from base_transfer_mapping m1 where m1.serverNumber=:serverNumber) m "
							+" on f.id = m.fileTransferId where m.serverNumber is null";
		
		Map<String,Object> totalDataParams = new HashMap<String,Object>();
		totalDataParams.put("serverNumber", serverNumber);
		try {
			Integer  dataTotal = fileTransferEntityDao.findDecimalNumberBySql(totalDataSql, totalDataParams);
			derbyPage.setDataTotal(dataTotal);
			
			int pageNumber = DerbyPageUtil.getPageNumber(dataTotal, ConstantUtil.DERBY_PAGE_PER_PAGE_NUMBER);
			derbyPage.setTotalPageNumber(pageNumber);
			
		} catch (SQLException e) {
			LoggerUtils.error("查询时出错了", e);
		}
		return derbyPage;
	}
	
	/**
	 * 文件下载
	 * @param request
	 */
	@RequestMapping(value = "/fileDownload", method = RequestMethod.GET)
	public void fileDownload(HttpServletRequest request,HttpServletResponse response) {
		
		String relativeFilePath = request.getParameter("relativeFilePath");
		String serverNumber = request.getParameter("serverNumber");
		FileTransferEntityDao<String, FileTransferEntity> fileTransferEntityDao = InstanceUtil.getFileTransferEntityDao();
		TransferMappingEntityDao<String, TransferMappingEntity> mappingDao = InstanceUtil.getTransferMappingEntityDao();
		
		String sql = "select f.* from base_file_transfer  f left join "
					 +" (select m1.* from base_transfer_mapping m1 where m1.serverNumber=:serverNumber) m "
					 +" on f.id = m.fileTransferId where m.serverNumber is null and relativeFilePath=:relativeFilePath";
		
		Map<String,Object> conditions = new HashMap<String,Object>();
		conditions.put("relativeFilePath", relativeFilePath);
		conditions.put("serverNumber", serverNumber);
		
		FileTransferEntity fileTransferEntity = null;
		FileInputStream fis = null;
		File file = null;
		Map<String,String> params = new HashMap<String,String>();
		try {
			synchronized (transferLock) {
				List<FileTransferEntity> FileTransferList = fileTransferEntityDao.findListByCondition(sql,conditions);
				if(FileTransferList != null && FileTransferList.size()>0){
					fileTransferEntity = FileTransferList.get(0);
					//保存映射实体
					TransferMappingEntity mapping = new TransferMappingEntity(FileUtil.getUUID(),fileTransferEntity.getId(),serverNumber);
					mappingDao.saveEntity(mapping);
					
					String absoluteFilePath = fileTransferEntity.getAbsoluteFilePath();
					file = new File(absoluteFilePath);
					fis = new FileInputStream(file);
				}
			}
			HttpClientUtil.downloadSingleFile(fis, (int)file.length(), params, request, response);
			
		} catch (Exception e1) {
			String filePath = "";
			if(fileTransferEntity != null){
				filePath = fileTransferEntity.getAbsoluteFilePath();
				try {
					//如果下载失败就删除对应的映射数据
					Map<String,Object> mappingParams = new HashMap<String,Object>();
					mappingParams.put("fileTransferId", fileTransferEntity.getId());
					mappingParams.put("serverNumber", serverNumber);
					
					List<TransferMappingEntity> findListByCondition = mappingDao.findListByCondition(mappingParams);
					for (TransferMappingEntity mapping : findListByCondition) {
						mappingDao.deleteEntity(mapping);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				fileTransferEntity = null;
			}
			LoggerUtils.error("被下载的文件下载失败,具体文件路径:"+filePath, e1);
		}finally{
			//关闭文件流
			if(fis != null){
				try {
					fis.close();
				} catch (IOException e) {
					LoggerUtils.error("文件流关闭失败!",e);
				}
			}
			
			//更改文件状态
			if(fileTransferEntity != null){
				fileTransferEntity.setFileTransferTime(new Date());
				try {
					fileTransferEntityDao.saveOrUpdateEntity(fileTransferEntity);
				} catch (SQLException e) {
					LoggerUtils.error("文件专题更新失败,文件路径:"+fileTransferEntity.getAbsoluteFilePath(), e);
				}
			}
		}
	}
	
	/**
	 * 静态资源下载
	 * @param request
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/staticsDownload", method = RequestMethod.GET)
	public void staticsDownload(final HttpServletRequest request,final HttpServletResponse response) {
		
		String relativeFilePath = request.getParameter("relativeFilePath");
		String absoluteFilePath = request.getParameter("absoluteFilePath");
		String serverNumber = request.getParameter("serverNumber");
		FileTransferEntityDao<String, FileTransferEntity> fileTransferEntityDao = InstanceUtil.getFileTransferEntityDao();
		TransferMappingEntityDao<String, TransferMappingEntity> mappingDao = InstanceUtil.getTransferMappingEntityDao();
		
		String sql = "select f.* from base_file_transfer  f left join "
				 +" (select m1.* from base_transfer_mapping m1 where m1.serverNumber=:serverNumber) m "
				 +" on f.id = m.fileTransferId where m.serverNumber is null and relativeFilePath=:relativeFilePath";
	
		Map<String,Object> conditions = new HashMap<String,Object>();
		conditions.put("relativeFilePath", relativeFilePath);
		conditions.put("serverNumber", serverNumber);
		
		FileTransferEntity fileTransferEntity = null;
		FileInputStream fis = null;
		final Map<String,String> params = new HashMap<String,String>();
		try {
			synchronized (transferLock) {
				List<FileTransferEntity> FileTransferList = fileTransferEntityDao.findListByCondition(sql,conditions);
				if(FileTransferList != null && FileTransferList.size()>0){
					fileTransferEntity = FileTransferList.get(0);
					//保存映射实体
					TransferMappingEntity mapping = new TransferMappingEntity(FileUtil.getUUID(),fileTransferEntity.getId(),serverNumber);
					mappingDao.saveEntity(mapping);
				}
			}
			
			HttpClientUtil.receiveSingleFile(absoluteFilePath, Collections.EMPTY_MAP,new HttpClientUtil().new ReceiveSingleFileCallBack() {
				@Override
				public void fileStreamCallBack(long contentLength,InputStream fileStream) {
					HttpClientUtil.downloadSingleFile(fileStream, (int)contentLength, params, request, response);
				}
			});
			
		} catch (Exception e1) {
			String filePath = "";
			if(fileTransferEntity != null){
				filePath = fileTransferEntity.getAbsoluteFilePath();
				try {
					//如果下载失败就删除对应的映射数据
					Map<String,Object> mappingParams = new HashMap<String,Object>();
					mappingParams.put("fileTransferId", fileTransferEntity.getId());
					mappingParams.put("serverNumber", serverNumber);
					
					List<TransferMappingEntity> findListByCondition = mappingDao.findListByCondition(mappingParams);
					for (TransferMappingEntity mapping : findListByCondition) {
						mappingDao.deleteEntity(mapping);
					}
				} catch (SQLException e) {
					LoggerUtils.error("",e);
				}
				fileTransferEntity = null;
			}
			LoggerUtils.error("被下载的文件下载失败,具体文件路径:"+filePath+" , 错误信息"+e1.getMessage());
			
		}finally{
			
			//关闭文件流
			if(fis != null){
				try {
					fis.close();
				} catch (IOException e) {
					LoggerUtils.error("文件流关闭失败!",e);
				}
			}
			
			//更改文件状态
			if(fileTransferEntity != null){
				fileTransferEntity.setFileTransferTime(new Date());
				try {
					fileTransferEntityDao.saveOrUpdateEntity(fileTransferEntity);
				} catch (SQLException e) {
					LoggerUtils.error("文件专题更新失败,文件路径:"+fileTransferEntity.getAbsoluteFilePath(), e);
				}
			}
		}
	}
	

}
