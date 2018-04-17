package com.bjhy.fbackup.statics.agent.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bjhy.fbackup.common.util.LoggerUtils;

@Component
public class StaticProvider implements StaticService{
	
	@Value("${static_root_path}")
	private String staticRootPath;
	
	@Override
	public void storeStatic(byte[] bytes, String path, String staticType) {
		File file = new File(staticRootPath + "/" + staticType + "/" + path);
		file.getParentFile().mkdirs();
		try (FileOutputStream fos = new FileOutputStream(file)){
			fos.write(bytes);
			fos.flush();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	

	@Override
	public void storeStatic(InputStream bytes, String path, String staticType) {
		File file = new File(staticRootPath + "/" + staticType + "/" + path);
		try {
			FileUtils.copyInputStreamToFile(bytes, file);
		} catch (IOException e) {
			LoggerUtils.error("写入磁盘是出错", e);
		}
	}

	@Override
	public void storeStatic(InputStream bytes, String path) {
		File file = new File(staticRootPath +"/" + path);
		try {
			FileUtils.copyInputStreamToFile(bytes, file);
		} catch (IOException e) {
			LoggerUtils.error("写入磁盘是出错", e);
		}
	}

	@Override
	public byte[] getStatic(String path, String staticType) {
		byte[] bytes = null;
		try (FileInputStream fis = new FileInputStream(staticRootPath + "/" + staticType + "/" + path)){
			bytes = new byte[fis.available()];
			fis.read(bytes);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return bytes;
	}
	
	@Override
	public byte[] getStatic(String path) {
		byte[] bytes = null;
		try (FileInputStream fis = new FileInputStream(staticRootPath + "/" + path)){
			bytes = new byte[fis.available()];
			fis.read(bytes);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return bytes;
	}

	@Override
	public void removeStatic(String path, String staticType) {
		File file = new File(staticRootPath + "/" + staticType + "/" + path);
		if(file.exists()){
			file.delete();
		}
	}
	
	@Override
	public void removeStatic(String path) {
		File file = new File(staticRootPath + "/" + path);
		if(file.exists()){
			file.delete();
		}
	}
}
