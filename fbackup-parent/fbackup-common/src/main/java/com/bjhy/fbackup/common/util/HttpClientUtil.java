package com.bjhy.fbackup.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.common.collect.Iterables;

/**
 * httpClient工具类
 * @author wubo
 *
 */
public class HttpClientUtil {
	/**
	 * 普通请求超时 10秒
	 */
	private static final int NORMAL_TIMEOUT = 10000;
	
	/**
	 * 文件操作超时 3 小时
	 */
	private static final int FILE_OPARATION_TIMEOUT = 10800000;
	
	/**
	 * 发送get http请求
	 * @param httpUrl
	 * @param clazz
	 * @return
	 */
	public static <T> T sendHttpGet(String httpUrl,Class<T> clazz){
		return sendHttpGet(httpUrl, null, clazz);
	}
	
	/**
	 * 发送get http请求
	 * @param httpUrl
	 * @param clazz
	 * @return
	 */
	public static <T> List<T> sendHttpGetList(String httpUrl,Class<T> clazz){
		return sendHttpGetList(httpUrl, null, clazz);
	}
	
	/**
	 * 发送get http请求
	 * @param httpUrl
	 * @param clazz
	 * @return
	 */
	public static <T> T sendHttpGet(String httpUrl,Map<String,String> params,Class<T> clazz){
		String data = executeGet(httpUrl, params, clazz);
		if(StringUtils.isEmpty(data)){
			return null;
		}
		T returnTypeEntity = GsonUtil.getReturnTypeEntity(data, clazz);
		return returnTypeEntity;
	}
	/**
	 * 发送get http请求
	 * @param httpUrl
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> sendHttpGetList(String httpUrl,Map<String,String> params,Class<T> clazz){
		String data = executeGet(httpUrl, params, clazz);
		if(StringUtils.isEmpty(data)){
			return Collections.EMPTY_LIST;
		}
		List<T> returnTypeListEntity = GsonUtil.getReturnTypeListEntity(data, clazz);
		return returnTypeListEntity;
	}
	
	/**
	 * 以流的方式发送文件
	 * @param url 请求发送的url
	 * @param is 文件流
	 * @param fileName 文件名
	 * @param params 其他的一些参数
	 * @param clazz 返回的类型Class
	 * @return 返回Class类型的数据
	 */
	public static <T> T sendSingleFile(String url,InputStream is,String fileName,Map<String, String> params,Class<T> clazz){
		T result = null;
		try (CloseableHttpClient httpclient = HttpClients.createDefault();){
			MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
			HttpEntity data = multipartEntityBuilder
					.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
					.setCharset(Charsets.UTF_8)
					.addBinaryBody("upfile", is, ContentType.DEFAULT_BINARY,fileName).build();
			
			//封装参数
			List<BasicNameValuePair> paramList = new ArrayList<BasicNameValuePair>();
			Set<Entry<String, String>> entrySet = params.entrySet();
			for (Entry<String, String> entry : entrySet) {
				paramList.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
			}
			
			//send http  
			HttpUriRequest request = RequestBuilder
					.post(url)
					.setCharset(Charsets.UTF_8)
					.addParameters(Iterables.toArray(paramList, BasicNameValuePair.class))
					.setEntity(data).build();
			
			CloseableHttpResponse response = httpclient.execute(request);
			String responseString = EntityUtils.toString(response.getEntity());
			result = GsonUtil.getGson().fromJson(responseString, clazz);
		} catch (Exception e) {
			LoggerUtils.error("执行该 "+url+" 出错",e);
		}
		return result;
	}
	
	/**
	 * 接收单个文件
	 * @param httpUrl 请求的url
	 * @param params 参数
	 * @return 返回文件流
	 */
	public static void receiveSingleFile(String httpUrl,Map<String,String> params,ReceiveSingleFileCallBack receiveSingleFileCallBack){
		httpUrl += getGetParmas(httpUrl,params);
		HttpGet httpGet = new HttpGet(httpUrl);
		//使用HttpClient，一般都需要设置连接超时时间和获取数据超时时间。这两个参数很重要，目的是为了防止访问其他http时，由于超时导致自己的应用受影响。
		RequestConfig requestConfig = RequestConfig.custom()  
		        .setConnectTimeout(NORMAL_TIMEOUT).setConnectionRequestTimeout(NORMAL_TIMEOUT)  
		        .setSocketTimeout(NORMAL_TIMEOUT).build();  
		httpGet.setConfig(requestConfig);
        
        try (CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().build();){
        	
			HttpResponse response=closeableHttpClient.execute(httpGet);
			 if(response.getStatusLine().getStatusCode()==200){
				 HttpEntity httpEntity = response.getEntity();
				 long contentLength = httpEntity.getContentLength();
				 if(contentLength == 0 && !CommonCenterUtil.getTransferZeroByteFile()){
					 String httpUrl2 = URLDecoder.decode(httpUrl, "utf-8");
					 throw new RuntimeException("当前  "+httpUrl2+" 没有返回任何文件流");
				 }
				 receiveSingleFileCallBack.fileStreamCallBack(contentLength,httpEntity.getContent());
			 }else{
				 String httpUrl2 = URLDecoder.decode(httpUrl, "utf-8");
				 throw new RuntimeException("当前  "+httpUrl2+"  请求出错 ,状态码是"+response.getStatusLine().getStatusCode());
			 }
		} catch (Exception e) {
			LoggerUtils.error("执行该 "+httpUrl+" 出错"+e.getMessage());
			throw new RuntimeException(e);
		} 
	}
	
	/**
	 * 文件上传工具类
	 * @param request
	 * @param uploadCallBack
	 */
	public static void uploadSingleFile(HttpServletRequest request,UploadCallBack uploadCallBack){
		/**
		 * 是否采用multipart的方式上传
		 */
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (!isMultipart) {
			return;
		}
		
		try {
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator fileItemIterator = upload.getItemIterator(request);
			while (fileItemIterator.hasNext()) {
				FileItemStream item = fileItemIterator.next();
				if (!item.isFormField()) {
					String fileName = item.getName();
					try (InputStream fileStream = item.openStream();) {
						uploadCallBack.fileStreamCallBack(fileStream, fileName);
					}
				}
			}
		} catch (FileUploadException | IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 下载单个文件(controller层时才用这个工具类)
	 * @param fileStream
	 * @param fileSize
	 * @param params
	 */
	public static void downloadSingleFile(InputStream fileStream,Integer fileSize,Map<String,String> params,HttpServletRequest request,HttpServletResponse response){

		int BUFFER_SIZE = 4096;
		InputStream in = null;
		OutputStream out = null;

		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			response.setContentType("application/octet-stream");

			response.setContentLength(fileSize);
			response.setHeader("Accept-Ranges", "bytes");

			int readLength = 0;

			in = new BufferedInputStream(fileStream, BUFFER_SIZE);
			out = new BufferedOutputStream(response.getOutputStream());

			byte[] buffer = new byte[BUFFER_SIZE];
			while ((readLength = in.read(buffer)) > 0) {
				byte[] bytes = new byte[readLength];
				System.arraycopy(buffer, 0, bytes, 0, readLength);
				out.write(bytes);
			}
			out.flush();

//			这个参数暂时没有用
//			response.addHeader("status", "1");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	/**
	 * 执行get请求并返回json数据
	 * @param httpUrl
	 * @param params
	 * @param clazz
	 * @return
	 */
	private static <T> String executeGet(String httpUrl,Map<String,String> params,Class<T> clazz){
		httpUrl += getGetParmas(httpUrl,params);
		HttpGet httpGet = new HttpGet(httpUrl);  
		//使用HttpClient，一般都需要设置连接超时时间和获取数据超时时间。这两个参数很重要，目的是为了防止访问其他http时，由于超时导致自己的应用受影响。
		RequestConfig requestConfig = RequestConfig.custom()  
		        .setConnectTimeout(NORMAL_TIMEOUT).setConnectionRequestTimeout(NORMAL_TIMEOUT)  
		        .setSocketTimeout(NORMAL_TIMEOUT).build();  
		httpGet.setConfig(requestConfig);
		//执行get请求  
		try (CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().build();){
			HttpResponse httpResponse = closeableHttpClient.execute(httpGet);
			
			if(httpResponse.getStatusLine().getStatusCode()==200){
				//获取响应消息实体  
				HttpEntity entity = httpResponse.getEntity(); 
				String data = EntityUtils.toString(entity);
				return data;
			}
		} catch (IOException e) {
			LoggerUtils.error("执行该 "+httpUrl+" 出错"+e.getMessage());
		}  
		return "";
	}
	
	/**
     * 得到get参数
     * @param params
     * @return
     */
    private static String getGetParmas(String httpUrl,Map<String,String> params){
    	if(params == null || params.isEmpty()){
    		return "";
    	}
    	
    	StringBuffer paramsBuffer = new StringBuffer();
    	Set<Entry<String, String>> entrySet = params.entrySet();
    	boolean isFirst = true;
    	for (Entry<String, String> entry : entrySet) {
    		if(isFirst){
    			isFirst = false;
    			if(httpUrl.contains("?")){
    				paramsBuffer.append("&"+entry.getKey()+"="+entry.getValue());
    			}else{
    				paramsBuffer.append("?"+entry.getKey()+"="+entry.getValue());
    			}
    		}else{
    			paramsBuffer.append("&"+entry.getKey()+"="+entry.getValue());
    		}
		}
    	return paramsBuffer.toString();
    }
    
    /**
     * 文件上传回调接口
     * @author wubo
     * @param <T>
     */
    public abstract class UploadCallBack{
    	
    	public abstract void fileStreamCallBack(InputStream fileStream,String fileName);
    	
    }
    
    /**
     * 接收单文件回调接口
     * @author wubo
     * @param <T>
     */
    public abstract class ReceiveSingleFileCallBack{
    	
    	/**
    	 * 文件流返回
    	 * @param contentLength 内容长度
    	 * @param fileStream 文件流
    	 */
    	public abstract void fileStreamCallBack(long contentLength,InputStream fileStream);
    	
    }

}  