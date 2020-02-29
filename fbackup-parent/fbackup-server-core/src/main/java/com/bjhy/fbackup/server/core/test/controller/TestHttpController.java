package com.bjhy.fbackup.server.core.test.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bjhy.fbackup.common.util.FileUtil;
import com.bjhy.fbackup.common.util.HttpClientUtil;

/**
 * 测试Http的controller
 * @author wubo
 */
@Controller
@RequestMapping("/test-http")
public class TestHttpController {
	
	@RequestMapping(value = "/sendGet")  
    public @ResponseBody Map<String, String> sendGet(HttpServletRequest request) {  
        Map<String, String> maps = new HashMap<String, String>();  
        String username = request.getParameter("username");  
        String password = request.getParameter("password");  
        maps.put("username", username);  
        maps.put("password", password);  
        return maps;  
    }  
	
	@RequestMapping(value = "/sendGet2")  
    public @ResponseBody List<Map<String, String>> sendGet2(HttpServletRequest request) {  
         
        String username = request.getParameter("username");  
        String password = request.getParameter("password");  
        
        List<Map<String,String>> returnList = new ArrayList<Map<String,String>>();
        
        Map<String, String> maps = new HashMap<String, String>(); 
        maps.put("username", username);  
        maps.put("password", password);  
        
        Map<String, String> maps2 = new HashMap<String, String>(); 
        maps2.put("username", username+"2");  
        maps2.put("password", password+"2");
        
        returnList.add(maps);
        returnList.add(maps2);
        return returnList;  
    } 
	
	/**
	 * 采用http调用的方式传输文件且是以流的方式
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/fileStore2", method = RequestMethod.POST)
	public Map<String,String> fileStore2(HttpServletRequest request) {
		System.out.println();
		HttpClientUtil.uploadSingleFile(request, new HttpClientUtil().new UploadCallBack(){
			@Override
			public void fileStreamCallBack(InputStream fileStream,String fileName) {
				System.out.println();
				try {
					FileUtils.copyInputStreamToFile(fileStream, new File("D:/temp/fileStream/456.png"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		});
		
		return null;
	}
	
	@RequestMapping(value = "/fileStore3", method = RequestMethod.POST)
	public void fileStore3(HttpServletRequest request, HttpServletResponse response){  
		  
        PrintWriter out = null;  
        response.setContentType("text/html;charset=UTF-8");  
          
        Map map = new HashMap();  
        FileItemFactory factory = new DiskFileItemFactory();  
        ServletFileUpload upload = new ServletFileUpload(factory);  
        File directory = null;    
        List<FileItem> items = new ArrayList();  
        try {  
            items = upload.parseRequest(request);  
            // 得到所有的文件  
            Iterator<FileItem> it = items.iterator();  
            while (it.hasNext()) {  
                FileItem fItem = (FileItem) it.next();  
                String fName = "";  
                Object fValue = null;  
                if (fItem.isFormField()) { // 普通文本框的值  
                    fName = fItem.getFieldName();  
//                  fValue = fItem.getString();  
                    fValue = fItem.getString("UTF-8");  
                    map.put(fName, fValue);  
                } else { // 获取上传文件的值  
                    fName = fItem.getFieldName();  
                    fValue = fItem.getInputStream();  
                    map.put(fName, fValue);  
                    String name = fItem.getName();  
                    if(name != null && !("".equals(name))) {  
                        name = name.substring(name.lastIndexOf(File.separator) + 1);  
                          
//                      String stamp = StringUtils.getFormattedCurrDateNumberString();  
                        String timestamp_Str =FileUtil.getLongId()+"";
                        directory = new File("d://test");    
                             directory.mkdirs();  
                          
                        String filePath = ("d://test")+ timestamp_Str+ File.separator + name;  
                        map.put(fName + "FilePath", filePath);  
                          
                        InputStream is = fItem.getInputStream();  
                        FileOutputStream fos = new FileOutputStream(filePath);  
                        byte[] buffer = new byte[1024];  
                        while (is.read(buffer) > 0) {  
                            fos.write(buffer, 0, buffer.length);  
                        }  
                        fos.flush();  
                        fos.close();  
                        map.put(fName + "FileName", name);  
                    }  
                }  
            }  
        } catch (Exception e) {  
            System.out.println("读取http请求属性值出错!");  
//          e.printStackTrace();  
        }  
          
        // 数据处理  
          
          
          
          
        try {  
            out = response.getWriter();  
            out.print("{success:true, msg:'接收成功'}");  
            out.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
  
  
    }  
	
	/**
	 * 返回文件流
	 * 
	 * @param request
	 */
	@RequestMapping(value = "/returnFileStream", method = RequestMethod.GET)
	public void returnFileStream(HttpServletRequest request,HttpServletResponse response) {
		File file = new File("D:/temp/fileStream/123.png");
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			
			Map<String,String> params = new HashMap<String,String>();
			params.put("fileName", "我是你大爷");
			params.put("fileSize", "123");
			
			HttpClientUtil.downloadSingleFile(fis, (int)file.length(), params, request, response);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
      
   
}
