package com.bjhy.fbackup.client.base;

import java.util.List;

/**
 * 读取图片类型的接口
 * @author wubo
 */
public interface ReadPictureType {
	
	/**
	 * 处理图片(静态资源)
	 * @param directoryType 目录类型
	 * @param pictureList 目录类型对应的配置数据
	 */
	public void dealWithPicture(String directoryType,List<String> pictureList);

}
