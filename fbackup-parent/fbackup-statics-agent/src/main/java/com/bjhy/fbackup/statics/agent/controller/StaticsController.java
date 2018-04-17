package com.bjhy.fbackup.statics.agent.controller;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bjhy.fbackup.common.util.HttpClientUtil;
import com.bjhy.fbackup.statics.agent.base.StaticService;
import com.bjhy.fbackup.statics.agent.base.StaticType;

@Controller
@RequestMapping("statics")
public class StaticsController {
	
	@Autowired
	private StaticService staticService;
	
//	/**
//	 * 存储静态资源
//	 * @param bytes 存储的资源字节数组
//	 * @param path 路径信息,该路径为一个相对路径地址,如路径为/foo/t1.jpg,此时如果
//	 * 获取的static.properties上存储地址为E:/statics，则最终路径为E:/statics/{staticType}/foo/t1.jpg
//	 * @param staticType 静态资源的类型,可通过{@link StaticType}静态变量获取
//	 */
//	void storeStatic(byte[] bytes, String path, String staticType);
	
	
	/**
	 * 存储静态资源
	 * @param bytes 存储的资源字节数组
	 * @param path 路径信息,该路径为一个相对路径地址,如路径为/foo/t1.jpg,此时如果
	 * 获取的static.properties上存储地址为E:/statics，则最终路径为E:/statics/{staticType}/foo/t1.jpg
	 * @param staticType 静态资源的类型,可通过{@link StaticType}静态变量获取
	 */
	@RequestMapping(value = "/storeStatic2", method = RequestMethod.POST)
	public @ResponseBody Map<String,String> storeStatic2(HttpServletRequest request) {
		Map<String,String> result = new HashMap<String,String>();
		try {
			final String path = request.getParameter("path");
			final String staticType = request.getParameter("staticType");
			
			HttpClientUtil.uploadSingleFile(request, new HttpClientUtil().new UploadCallBack(){
				@Override
				public void fileStreamCallBack(InputStream fileStream,String fileName) {
					staticService.storeStatic(fileStream, path, staticType);
				}
			});
		} catch (Exception e) {
			result.put("returnStatus", "success");
			result.put("message", e.getMessage());
			return result;
		}
		
		result.put("returnStatus", "success");
		result.put("message", "ok");
		
		return result;
	}
	
	/**
	 * 存储静态资源
	 * @param bytes 存储的资源字节数组
	 * @param path 路径信息,该路径为一个相对路径地址,如路径为/foo/t1.jpg,此时如果
	 * 获取的static.properties上存储地址为E:/statics，则最终路径为E:/statics/{staticType}/foo/t1.jpg
	 * @param staticType 静态资源的类型,可通过{@link StaticType}静态变量获取
	 */
	@RequestMapping(value = "/storeStatic3", method = RequestMethod.POST)
	public @ResponseBody Map<String,String> storeStatic3(HttpServletRequest request) {
		Map<String,String> result = new HashMap<String,String>();
		try {
			final String path = request.getParameter("path");
			
			HttpClientUtil.uploadSingleFile(request, new HttpClientUtil().new UploadCallBack(){
				@Override
				public void fileStreamCallBack(InputStream fileStream,String fileName) {
					staticService.storeStatic(fileStream, path);
				}
			});
		} catch (Exception e) {
			result.put("returnStatus", "success");
			result.put("message", e.getMessage());
			return result;
		}
		
		result.put("returnStatus", "success");
		result.put("message", "ok");
		
		return result;
	}
	
//	/**
//	 * 获取静态资源
//	 * @param path 路径信息,该路径为一个相对路径地址,如路径为/foo/t1.jpg,此时如果
//	 * 获取的static.properties上存储地址为E:/statics，则最终路径为E:/statics/{staticType}/foo/t1.jpg
//	 * @param staticType 静态资源的类型,可通过{@link StaticType}静态变量获取
//	 * @return 资源字节数组
//	 */
//	byte[] getStatic(String path, String staticType);
//	
//	/**
//	 * 获取静态资源
//	 * @param path 路径信息,该路径为一个相对路径地址,如路径为/foo/t1.jpg,此时如果
//	 * 获取的static.properties上存储地址为E:/statics，则最终路径为E:/statics/foo/t1.jpg
//	 * @return 资源字节数组
//	 */
//	byte[] getStatic(String path);
//
//	/**
//	 * 删除静态资源
//	 * @param path 路径信息,该路径为一个相对路径地址,如路径为/foo/t1.jpg,此时如果
//	 * 获取的static.properties上存储地址为E:/statics，则最终路径为E:/statics/{staticType}/foo/t1.jpg
//	 * @param staticType 静态资源的类型,可通过{@link StaticType}静态变量获取
//	 */
//	void removeStatic(String path, String staticType);
//	
//	/**
//	 * 删除静态资源
//	 * @param path 路径信息,该路径为一个相对路径地址,如路径为/foo/t1.jpg,此时如果
//	 * 获取的static.properties上存储地址为E:/statics，则最终路径为E:/statics/foo/t1.jpg
//	 */
//	void removeStatic(String path);

}
