package com.bjhy.fbackup.client.core.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.bjhy.fbackup.common.util.HttpClientUtil;

public class TestHttpClient {
	
	public static void main(String[] args) throws IOException {
		testFileUpload();
//		SubmitPost();
		System.out.println("完毕!!");
		System.in.read();
	}
	
	public static void testFileUpload(){
		String http = "http://localhost:5555/fbackup-server/test-http/fileStore2";
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("downloadNumber", "5555");
		params.put("downloadSize", "888888");
		
		FileInputStream fis;
		try {
			fis = new FileInputStream(new File("D:/temp/398.png"));
			HttpClientUtil.sendSingleFile(http, fis, "xxxxx", params, Map.class);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
	}
	
	 public static void SubmitPost(){
		 String http = "http://localhost:5555/fbackup-server/test-http/fileStore3";
		 TestHttpClient httpPostArgumentTest2 = new TestHttpClient();  
         
	        httpPostArgumentTest2.SubmitPost(http, "398.png");  
	 }
	
	  //file1与file2在同一个文件夹下 filepath是该文件夹指定的路径      
    public void SubmitPost(String url,String filename1){  
          
        HttpClient httpclient = new DefaultHttpClient();  
          
        try {  
      
            HttpPost httppost = new HttpPost(url);  
              
            FileBody bin = new FileBody(new File("D:/temp/398.png"));  
                
            FileBody bin2 = new FileBody(new File("D:/temp/398.png"));  
            StringBody comment = new StringBody(filename1);  
  
            MultipartEntity reqEntity = new MultipartEntity();  
              
            reqEntity.addPart("file1", bin);//file1为请求后台的File upload;属性      
             reqEntity.addPart("file2", bin2);//file2为请求后台的File upload;属性  
             reqEntity.addPart("filename1", comment);//filename1为请求后台的普通参数;属性     
            httppost.setEntity(reqEntity);  
              
            HttpResponse response = httpclient.execute(httppost);  
              
                  
            int statusCode = response.getStatusLine().getStatusCode();  
              
                  
            if(statusCode == HttpStatus.SC_OK){  
                      
                System.out.println("服务器正常响应.....");  
                  
                HttpEntity resEntity = response.getEntity();  
                  
                  
                System.out.println(EntityUtils.toString(resEntity));//httpclient自带的工具类读取返回数据  
                  
                  
                  
                System.out.println(resEntity.getContent());     
  
                EntityUtils.consume(resEntity);  
            }  
                  
            } catch (ParseException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            } catch (IOException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            } finally {  
                try {   
                    httpclient.getConnectionManager().shutdown();   
                } catch (Exception ignore) {  
                      
                }  
            }  
        }  
	
	public static void testFileDownload(){
		String http = "http://localhost:5555/fbackup-server/test-http/returnFileStream";
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("downloadNumber", "5");
		params.put("downloadSize", "888");
		
		HttpClientUtil.receiveSingleFile(http, params,new HttpClientUtil().new ReceiveSingleFileCallBack() {
			
			@Override
			public void fileStreamCallBack(long contextLength,InputStream fileStream) {
				try {
					FileUtils.copyInputStreamToFile(fileStream, new File("D:/temp/fileStream/789.png"));					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
			
	}
	
	public static void testGet(){
		String http = "http://localhost:5555/fbackup-server/test-http/sendGet2";
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("username", "xxx");
		params.put("password", "yyyy");
		
		List<User> returnTypeEntity = HttpClientUtil.sendHttpGetList(http,params,User.class);
		for (User user : returnTypeEntity) {
			System.out.println(user);
		}
		System.out.println(returnTypeEntity);
	}
	
	public class User{
		private String password;
		private String username;
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		
	}
}
