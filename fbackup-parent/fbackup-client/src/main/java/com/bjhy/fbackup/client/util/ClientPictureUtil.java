package com.bjhy.fbackup.client.util;

import java.util.ArrayList;
import java.util.List;

import com.bjhy.fbackup.client.core.domain.DirectoryTypePicture;
import com.bjhy.fbackup.client.service.FbackupPictureService;
import com.bjhy.fbackup.common.domain.DirectoryInfo;
import com.bjhy.fbackup.common.domain.XmlFbackup;
import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.common.util.FileUtil;
import com.bjhy.fbackup.common.util.GsonUtil;

/**
 * 客户端图片工具类
 * @author wulin
 *
 */
public class ClientPictureUtil {
	
	/**
	 * 资源对象
	 */
	private static XmlFbackup xmlFbackup = ExtensionLoader.getInstance(XmlFbackup.class);
	
	/**
	 * 得到picture的配置目录
	 * @return
	 */
	public static List<String> getPictureList(){
		List<String> list = new ArrayList<String>();
		List<DirectoryInfo> directoryList = xmlFbackup.getXmlClient().getDirectoryList();
		
		for (DirectoryInfo directoryInfo : directoryList) {
			if(FbackupPictureService.DIRECTORY_TYPE_PICTURE.equals(directoryInfo.getDirectoryType())) {
				list.add(directoryInfo.getContent());
			}
		}
		return list;
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

}
