package com.bjhy.fbackup.server.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.bjhy.fbackup.common.domain.ClientFileTransfer;
import com.bjhy.fbackup.common.domain.XmlFbackup;
import com.bjhy.fbackup.common.domain.XmlServer;
import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.common.util.FileUtil;
import com.bjhy.fbackup.common.util.LoggerUtils;

/**
 * 服务端文件类型的处理
 * @author wubo
 */
public class ServerFileUtil {
	
	/**
	 * 文件的存储
	 */
	private static final XmlFbackup server = ExtensionLoader.getInstance(XmlFbackup.class);
	
	/**
	 * 得到根文件路径
	 * @return
	 */
	private static List<String> getRootFileDirectory(){
		List<String> filePaths = server.getXmlServer().getStoreDirectory().get(XmlServer.STORE_TYPE_FILE);
		return filePaths;
	}
	
	/**
	 * 文件存储
	 * @param is
	 * @param fileRelativePath
	 */
	public static void fileStore(InputStream is,ClientFileTransfer clientFileTransfer){
		String fileRelativePath = clientFileTransfer.getRelativeFilePath();
		String clientNumber = clientFileTransfer.getClientNumber();
		String clientName = clientFileTransfer.getClientName();
		String client = clientName+"("+clientNumber+")";
		List<String> rootFileDirectory = getRootFileDirectory();
		for (String rootDirectory : rootFileDirectory) {
			rootDirectory = getYearMonthDayDirectory(rootDirectory)+"/"+client;
			createFileDirectory(rootDirectory);
			String filePath = FileUtil.replaceSpritAndEnd(rootDirectory)+fileRelativePath;
			File file = new File(filePath);
			try {
				FileUtils.copyInputStreamToFile(is, file);
			} catch (IOException e) {
				LoggerUtils.error("文件存储失败", e);
			}
		}
	}
	
	/**
	 * 得到年月日的根目录
	 * @param rootDirectory
	 * @return
	 */
	private static String getYearMonthDayDirectory(String rootDirectory){
		return rootDirectory+"/"+FileUtil.getYearMonthDay();
	}
	
	/**
	 * 清理7天前的目录
	 */
	public static void cleanSevenDayDirectory(){
		List<String> rootFileDirectory = getRootFileDirectory();
		for (String rootDirectory : rootFileDirectory) {
			rootDirectory = FileUtil.replaceSprit(rootDirectory);
			File file = new File(rootDirectory);
			if(file.exists() && file.isDirectory()){
				File[] listFiles = file.listFiles(new FileFilter(){
					@Override
					public boolean accept(File pathname) {
						String currentDirectory = pathname.getName();
						String SevenDirectory = FileUtil.getBeforeSevenDirectory();
						int currentDirectoryInt = 0;
						int SevenDirectoryInt = 0;
								
						try {
							currentDirectoryInt = Integer.parseInt(currentDirectory);
							SevenDirectoryInt = Integer.parseInt(SevenDirectory);
						} catch (NumberFormatException e) {
							return false;
						}
						
						if(currentDirectoryInt<=SevenDirectoryInt){
							return true;
						}
						return false;
					}
				});
				
				for (File file2 : listFiles) {
					try {
						FileUtil.deleteFile(file2.getAbsolutePath());
					} catch (Exception e) {
						LoggerUtils.error(file2.getAbsolutePath()+"文件删除失败"+e.getMessage());
					}
				}
			}
		}
	}
	
	/**
	 * 创建文件目录
	 * @param rootDirectory 更目录
	 */
	private static void createFileDirectory(String rootDirectory){
		File file = new File(FileUtil.replaceSpritAndEnd(rootDirectory));
		if(!file.exists()){
			file.mkdirs();
		}
	}
}
