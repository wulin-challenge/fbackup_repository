package com.bjhy.fbackup.statics.agent.base;

import java.io.InputStream;

/**
 * 静态服务公开接口
 * 用于对应用系统提供对js，css，图片，视频等资源进行静态存储和提取的服务。
 * 静态服务需要依靠一台web静态服务器配合，才能够达到静态资源提取的行为效果，存储地址配置在
 * 接口实现工程的类路径下的static.properties文件中。在使用静态服务工程中如果需要在前台界面上通过
 * img标签直接展示数据;标签直接显示图片信息，则需要在app.properties中配置static_service_url的web静态服务器地址，
 * 如:static_service_url=http://192.168.0.49:9898。获取该地址有两种方式：
 * 1.页面导入freemarker变量${static_service_url}，直接在js中可以使用变量static_service_url。
 * 2.可以直接通过${global_static_service_url}获取servletContext作用域的web静态资源地址。
 * 拿到地址之后只用使用存储的相对地址配合上静态web服务器资源地址就可以直接输出img标签的数据
 * 推荐使用nginx作为web静态服务器配合提供静态资源服务
 * @author lijian
 *
 */
public interface StaticService {
	
	/**
	 * 存储静态资源
	 * @param bytes 存储的资源字节数组
	 * @param path 路径信息,该路径为一个相对路径地址,如路径为/foo/t1.jpg,此时如果
	 * 获取的static.properties上存储地址为E:/statics，则最终路径为E:/statics/{staticType}/foo/t1.jpg
	 * @param staticType 静态资源的类型,可通过{@link StaticType}静态变量获取
	 */
	void storeStatic(byte[] bytes, String path, String staticType);
	void storeStatic(InputStream bytes, String path, String staticType);
	void storeStatic(InputStream bytes, String path);
	
	/**
	 * 获取静态资源
	 * @param path 路径信息,该路径为一个相对路径地址,如路径为/foo/t1.jpg,此时如果
	 * 获取的static.properties上存储地址为E:/statics，则最终路径为E:/statics/{staticType}/foo/t1.jpg
	 * @param staticType 静态资源的类型,可通过{@link StaticType}静态变量获取
	 * @return 资源字节数组
	 */
	byte[] getStatic(String path, String staticType);
	
	/**
	 * 获取静态资源
	 * @param path 路径信息,该路径为一个相对路径地址,如路径为/foo/t1.jpg,此时如果
	 * 获取的static.properties上存储地址为E:/statics，则最终路径为E:/statics/foo/t1.jpg
	 * @return 资源字节数组
	 */
	byte[] getStatic(String path);

	/**
	 * 删除静态资源
	 * @param path 路径信息,该路径为一个相对路径地址,如路径为/foo/t1.jpg,此时如果
	 * 获取的static.properties上存储地址为E:/statics，则最终路径为E:/statics/{staticType}/foo/t1.jpg
	 * @param staticType 静态资源的类型,可通过{@link StaticType}静态变量获取
	 */
	void removeStatic(String path, String staticType);
	
	/**
	 * 删除静态资源
	 * @param path 路径信息,该路径为一个相对路径地址,如路径为/foo/t1.jpg,此时如果
	 * 获取的static.properties上存储地址为E:/statics，则最终路径为E:/statics/foo/t1.jpg
	 */
	void removeStatic(String path);
	
}
