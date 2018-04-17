package com.bjhy.fbackup.server.build;

import org.apel.gaia.container.boot.PlatformStarter;



/**
 * 启动服务
 * @author wubo
 */
public class StartServer {
	
	public static void main(String[] args) {
//		org.apel.gaia.container.boot.PlatformStarter.start(args);
//		PlatformStarter.start();
		PlatformStarter.start(args);
		System.out.println("启动成功");
	}

}
