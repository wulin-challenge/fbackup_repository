package com.bjhy.fbackup.client.build;

import org.apel.gaia.container.boot.PlatformStarter;

import com.bjhy.fbackup.client.core.init.StartInit;

/**
 * 客户端启动类
 * @author wubo
 *
 */
public class ClientStart {
	
	public static void main(String[] args) {
		PlatformStarter.start(args);
		StartInit.init();
		System.out.println("客户端启动成功!!");
	}

}
