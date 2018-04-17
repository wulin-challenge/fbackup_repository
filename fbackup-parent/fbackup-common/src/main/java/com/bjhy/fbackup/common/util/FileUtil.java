package com.bjhy.fbackup.common.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

/**
 * 文件工具类
 * @author wubo
 *
 */
public class FileUtil {
	
	/**
	 * longId生成锁
	 */
	private static Object idLock = new Object();
	
	/**
	 * 替换反斜杠,解决在windows,linux下的路径问题
	 * @param path
	 * @return
	 */
	public static String replaceSprit(String path){
		if(StringUtils.isEmpty(path)){
			return "";
		}
		path = path.replace("\\\\", "/"); //// Java中4个反斜杠表示一个反斜杠
		path = path.replace("\\", "/"); 
		return path;
	}
	
	/**
	 * 替换反斜杠,解决在windows,linux下的路径问题,并且以"/"结束
	 * @param path
	 * @return
	 */
	public static String replaceSpritAndEnd(String path){
		if(StringUtils.isEmpty(path)){
			return "";
		}
		path = path.replace("\\\\", "/"); //// Java中4个反斜杠表示一个反斜杠
		path = path.replace("\\", "/"); 
		
		if(!path.endsWith("/")){
			path+="/";
		}
		return path;
	}
	
	/**
	 * 替换反斜杠,解决在windows,linux下的路径问题,并且不能以"/"结束
	 * @param path
	 * @return
	 */
	public static String replaceSpritAndNotEnd(String path){
		if(StringUtils.isEmpty(path)){
			return "";
		}
		path = path.replace("\\\\", "/"); //// Java中4个反斜杠表示一个反斜杠
		path = path.replace("\\", "/"); 
		
		if(path.endsWith("/")){
			path = path.substring(0, path.length()-1);
		}
		return path;
	}
	
	/**
	 * 得到文件监听器的文件路径
	 * @param watckKey
	 * @param event
	 * @return
	 */
	public static String getFilePathOfFileWatcher(WatchKey watckKey,WatchEvent<?> event){
		Path path = (Path) watckKey.watchable();
        Path fileName = (Path) event.context();
        
        String filePath = path.toString()+"/"+fileName.toString();
        filePath =  FileUtil.replaceSprit(filePath);
        return filePath;
	}
	/**
	 * 得到文件监听器的文件路径
	 * @param watckKey
	 * @param event
	 * @return
	 */
	public static String getRelativeDirectory(String path){
		if(StringUtils.isBlank(path)){
			return "";
		}
		path = FileUtil.replaceSprit(path);
		int startIndex = path.lastIndexOf("/");
		return path.substring(startIndex);
		
	}
	
	/**
	 * 得到uuid
	 * @return
	 */
	public static String getUUID(){
		String uuid = UUID.randomUUID().toString().toUpperCase().replace("-", "");
		return uuid;
	}
	
	/**
	 * long型id
	 * @return
	 */
	public static long getLongId(){
		synchronized (idLock) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			String format = sdf.format(new Date());
			return Long.parseLong(format);
		}
	}
	
	/**
	 * YearMonthDay的路径
	 * @return
	 */
	public static String getYearMonthDay(){
		synchronized (idLock) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String format = sdf.format(new Date());
			return format;
		}
	}
	/**
	 * YearMonthDay的路径
	 * @return
	 */
	public static String getYearMonthDay(Date date){
		synchronized (idLock) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String format = sdf.format(date);
			return format;
		}
	}
	
	/**
	 * 得到七天前的目录
	 * @return
	 */
	public static String getBeforeSevenDirectory(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DAY_OF_MONTH, -7);
		Date date = calendar.getTime();
		return getYearMonthDay(date);
	}
	
	/**
	 * 删除某个文件夹及该文件夹下的所有文件及文件夹
	 * @param delpath
	 * @return
	 * @throws Exception
	 */
	public static boolean deleteFile(String delpath) throws Exception{
		try {
			delpath = delpath.replace("\\\\", "/"); //// Java中4个反斜杠表示一个反斜杠
			delpath = delpath.replace("\\", "/"); 
			File file = new File(delpath);
			// 当且仅当此抽象路径名表示的文件存在且 是一个目录时，返回 true
			if (!file.isDirectory()) {
				file.delete();
			} else if (file.isDirectory()) {
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File delfile = new File(delpath + "/" + filelist[i]);
					if (!delfile.isDirectory()) {
						delfile.delete();
						System.out.println(delfile.getAbsolutePath() + "删除文件成功");
					} else if (delfile.isDirectory()) {
						deleteFile(delpath + "/" + filelist[i]);
					}
				}
				System.out.println(file.getAbsolutePath() + "删除成功");
				file.delete();
			}

		} catch (FileNotFoundException e) {
			System.out.println("deletefile() Exception:" + e.getMessage());
		}
		return true;
	}
	
}
