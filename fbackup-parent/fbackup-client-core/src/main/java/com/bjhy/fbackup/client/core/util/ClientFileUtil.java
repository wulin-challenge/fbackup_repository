package com.bjhy.fbackup.client.core.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import com.bjhy.fbackup.common.domain.DirectoryInfo;
import com.bjhy.fbackup.common.domain.XmlClient;
import com.bjhy.fbackup.common.domain.XmlFbackup;
import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.common.util.FileUtil;

/**
 * 客户端客户端文件类型工具类
 * @author wubo
 *
 */
public class ClientFileUtil {
	
	/**
	 * 加载配置文件
	 */
	private static final XmlFbackup fbackup = ExtensionLoader.getInstance(XmlFbackup.class);
	
	public static XmlFbackup getFbackup() {
		return fbackup;
	}

	/**
	 * 得到文件类型的所有目录
	 * @return
	 */
	public static List<String> getFileDirectoryList(){
		List<String> list = new ArrayList<String>();
		List<DirectoryInfo> directoryList = fbackup.getXmlClient().getDirectoryList();
		
		for (DirectoryInfo directoryInfo : directoryList) {
			if(XmlClient.DIRECTORY_TYPE_FILE.equals(directoryInfo.getDirectoryType())) {
				list.add(directoryInfo.getContent());
			}
		}
		return list;
	}
	

	/**
	 * 通过绝对路径得到相对路径
	 * @param absolutePath
	 * @return
	 */
	public static String getFileRelativePath(String absolutePath){
		if(StringUtils.isBlank(absolutePath)){
			return "";
		}
		
		absolutePath = FileUtil.replaceSprit(absolutePath);
		for (String rootDirectory : getFileDirectoryList()) {
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
	public static void readFiles(List<DirectoryInfo> directoryInfoList,SingleFile singleFile){
		for (DirectoryInfo directoryInfo : directoryInfoList) {
			try {
				File file = new File(directoryInfo.getContent());
				readFiles(directoryInfo,file,singleFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	/**
	 * 读取文件
	 */
	private static void readFiles(DirectoryInfo directoryInfo,File file,SingleFile singleFile) throws Exception{
		if(file.isDirectory()){
			File[] listFiles = file.listFiles();
			for (File file2 : listFiles) {
				readFiles(directoryInfo,file2,singleFile);
			}
		}else if(file.isFile()){
			String directoryPath2 = FileUtil.replaceSprit(directoryInfo.getContent());
			String absolutePath = FileUtil.replaceSprit(file.getAbsolutePath());
			String relativePath = absolutePath.substring(directoryPath2.length());
			singleFile.oneFile(directoryInfo,file,relativePath);
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
		 * @param directoryInfo 文件信息
		 * @param oneFile 具体的文件对象
		 * @param relativePath 相对路径
		 */
		public abstract void oneFile(DirectoryInfo directoryInfo,File oneFile,String relativePath) throws Exception;
	}

}
