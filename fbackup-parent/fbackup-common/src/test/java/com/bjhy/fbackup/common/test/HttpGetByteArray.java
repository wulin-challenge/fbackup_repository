package com.bjhy.fbackup.common.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import org.apache.commons.io.FileUtils;

import com.bjhy.fbackup.common.util.HttpClientUtil;
@SuppressWarnings("unchecked")
public class HttpGetByteArray {
	static String http = "http://192.168.0.251:9898/images/criminalPhotos/2215018236/2215018236_code_mtlb_1_1_1.jpg";
	
	
	public static void main(String[] args) {
		System.out.println();
		
		HttpClientUtil.receiveSingleFile(http, Collections.EMPTY_MAP,new HttpClientUtil().new ReceiveSingleFileCallBack() {
			@Override
			public void fileStreamCallBack(long contentLenght,InputStream fileStream) {
				try {
					FileUtils.copyInputStreamToFile(fileStream, new File("D:/temp/fbackup/statics/1.png"));
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println();
			}
		});
	}

}
