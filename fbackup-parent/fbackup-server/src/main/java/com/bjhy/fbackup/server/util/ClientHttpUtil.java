package com.bjhy.fbackup.server.util;
import com.bjhy.fbackup.common.domain.XmlFbackup;

public class ClientHttpUtil {
	
	/**
	 * 得到 PageclientFiles 分页httpUrl
	 * @param client
	 * @param clientHttpUrl
	 * @return
	 */
	public static String getPageclientFilesHttpUrl(XmlFbackup client,StringBuffer clientHttpUrl,String serverNumber){
		String httpUrl = clientHttpUrl.toString()+"/getPageclientFiles?serverNumber="+serverNumber;
		return httpUrl;
	}
	
	/**
	 * 得到 DerbyPage 的分页httpUrl
	 * @param client
	 * @param clientHttpUrl
	 * @return
	 */
	public static String getDerbyPageHttpUrl(XmlFbackup client,StringBuffer clientHttpUrl,String serverNumber){
		String httpUrl = clientHttpUrl.toString()+"/getDerbyPage?serverNumber="+serverNumber;
		return httpUrl;
	}
	
	/**
	 * 得到 fileDownload 的下载httpUrl
	 * @param client
	 * @param clientHttpUrl
	 * @param relativeFilePath 相对文件路径
	 * @param serverNumber 服务器的编号
	 * @return
	 */
	public static String getFileDownloadUrl(XmlFbackup client,StringBuffer clientHttpUrl,String relativeFilePath,String serverNumber){
		String httpUrl = clientHttpUrl.toString()+"/fileDownload?relativeFilePath="+relativeFilePath+"&serverNumber="+serverNumber;
		return httpUrl;
	}
	
	/**
	 * 得到 checkFileStatus 的检测client端的文件状态
	 * @param client
	 * @param clientHttpUrl
	 * @param relativeFilePath 相对文件路径
	 * @param serverNumber 服务器的编号
	 * @return
	 */
	public static String getCheckClientFileStatusUrl(XmlFbackup client,StringBuffer clientHttpUrl,String relativeFilePath,String serverNumber){
		String httpUrl = clientHttpUrl.toString()+"/checkFileStatus?relativeFilePath="+relativeFilePath+"&serverNumber="+serverNumber;
		return httpUrl;
	}
	
	/**
	 * 得到 staticsDownload 的下载httpUrl
	 * @param client
	 * @param clientHttpUrl
	 * @param relativeFilePath 相对文件路径
	 * @param absoluteFilePath 绝对路径(httpUrl)
	 * @return
	 */
	public static String getStaticsDownloadUrl(XmlFbackup client,StringBuffer clientHttpUrl,String relativeFilePath,String absoluteFilePath,String serverNumber){
		String httpUrl = clientHttpUrl.toString()+"/staticsDownload?relativeFilePath="+relativeFilePath+"&absoluteFilePath="+absoluteFilePath+"&serverNumber="+serverNumber;
		return httpUrl;
	}
	
	/**
	 * 得到客户端的httpUrl
	 * @param client
	 * @return
	 */
	public static StringBuffer getClientHttpUrl(XmlFbackup client){
		StringBuffer httpUrl = new StringBuffer("http://");
		httpUrl.append(client.getServerIp()+":"+client.getServerPort()+"/"+client.getServerContext()+"/client");
		return httpUrl;
	}

}
