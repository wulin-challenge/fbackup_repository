package com.bjhy.fbackup.client.base;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.bjhy.fbackup.client.domain.DirectoryTypePicture;
import com.bjhy.fbackup.common.domain.XmlClient;
import com.bjhy.fbackup.common.domain.XmlFbackup;
import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.common.util.FileUtil;
import com.bjhy.fbackup.common.util.GsonUtil;

/**
 * 客户端存储/读取路径
 * @author wubo
 *
 */
public class ClientStorePath {
	
	/**
	 * 加载配置文件
	 */
	private static final XmlFbackup fbackup = ExtensionLoader.getInstance(XmlFbackup.class);
	
	/**
	 * 得到所有的读取目录
	 * @return
	 */
	private static Map<String,List<String>> getAllReadDirectory(){
		return fbackup.getXmlClient().getReadDirectory();
	}
	
	/**
	 * 得到picture的配置目录
	 * @return
	 */
	public static List<String> getPictureList(){
		Map<String, List<String>> allReadDirectory = getAllReadDirectory();
		List<String> pictureList = allReadDirectory.get(XmlClient.DIRECTORY_TYPE_PICTURE);
		return pictureList;
	}
	
	/**
	 * 通过静态类型
	 * @param staticType 静态类型
	 * @return
	 */
	public static DirectoryTypePicture getPictureByStaticType(String staticType){
		List<String> pictureList = getPictureList();
		if(pictureList == null){
			return null;
		}
		
		for (String pictureJson : pictureList) {
			pictureJson = FileUtil.replaceSpritAndNotEnd(pictureJson);
			List<DirectoryTypePicture> returnTypeListEntity = GsonUtil.getReturnTypeListEntity(pictureJson, DirectoryTypePicture.class);
			System.out.println();
			System.out.println();
			for (DirectoryTypePicture directoryTypePicture : returnTypeListEntity) {
				String staticType2 = directoryTypePicture.getStaticType();
				if(staticType2.trim().equalsIgnoreCase(staticType.trim())){
					return directoryTypePicture;
				}
			}
		}
		return null;
	}
	

	/**
	 * 得到database的配置目录
	 * @return
	 */
	public static List<String> getDatabaseList(){
		Map<String, List<String>> allReadDirectory = getAllReadDirectory();
		List<String> databaseList = allReadDirectory.get(XmlClient.DIRECTORY_TYPE_DATABASE);
		return databaseList;
	}
	
	/**
	 * 通过绝对路径得到相对路径
	 * @param absolutePath
	 * @return
	 */
	public static String getDatabaseRelativePath(String absolutePath){
		if(StringUtils.isBlank(absolutePath)){
			return "";
		}
		
		absolutePath = FileUtil.replaceSprit(absolutePath);
		List<String> databaseList = getDatabaseList();
		for (String rootDirectory : databaseList) {
			rootDirectory = FileUtil.replaceSpritAndEnd(rootDirectory);
			if(absolutePath.contains(rootDirectory)){
				int startIndex = absolutePath.indexOf(rootDirectory)+rootDirectory.length();
				String relativePath = absolutePath.substring(startIndex);
				return relativePath;
			}
		}
		return "";
	}
	
	/**
	 * 读取文件
	 */
	public static void readFiles(SingleFile singleFile){
//		Map<String, List<String>> allReadDirectory = getAllReadDirectory();
//		Set<Entry<String, List<String>>> entrySet = allReadDirectory.entrySet();
//		for (Entry<String, List<String>> entry : entrySet) {
//			String fileType = entry.getKey();
//			List<String> directoryPathList = entry.getValue();
//			for (String directoryPath : directoryPathList) {
//				File file = new File(directoryPath);
//				readFiles(directoryPath,fileType,file,singleFile);
//			}
//		}
		
		List<String> databaseList = getDatabaseList();
		for (String directoryPath : databaseList) {
			try {
				File file = new File(directoryPath);
				readFiles(directoryPath,XmlClient.DIRECTORY_TYPE_DATABASE,file,singleFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	/**
	 * 读取文件
	 */
	private static void readFiles(String directoryPath,String fileType,File file,SingleFile singleFile) throws Exception{
		if(file.isDirectory()){
			File[] listFiles = file.listFiles();
			for (File file2 : listFiles) {
				readFiles(directoryPath,fileType,file2,singleFile);
			}
		}else if(file.isFile()){
			String directoryPath2 = FileUtil.replaceSprit(directoryPath);
			String absolutePath = FileUtil.replaceSprit(file.getAbsolutePath());
			String relativePath = absolutePath.substring(directoryPath2.length());
			singleFile.oneFile(fileType,file,relativePath);
		}
	}
	
	/**
	 * 单个文件
	 * @author wubo
	 *
	 */
	public static abstract class SingleFile{
		/**
		 * 一个文件
		 * @param fileType 文件类型
		 * @param oneFile 具体的文件对象
		 * @param relativePath 相对路径
		 */
		public abstract void oneFile(String fileType,File oneFile,String relativePath) throws Exception;
	}

}
