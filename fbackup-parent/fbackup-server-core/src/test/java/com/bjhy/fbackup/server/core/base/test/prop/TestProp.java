package com.bjhy.fbackup.server.core.base.test.prop;

import java.util.List;

import com.bjhy.fbackup.common.util.CenterPropUtil;

public class TestProp {
	
	public static void main(String[] args) {
		List<String> propertyList = CenterPropUtil.getPropertyList("sync_client");
		String sync_client = CenterPropUtil.getProperty("sync_client");
		
		
		System.out.println(sync_client);
		System.out.println(propertyList);
	}

}
