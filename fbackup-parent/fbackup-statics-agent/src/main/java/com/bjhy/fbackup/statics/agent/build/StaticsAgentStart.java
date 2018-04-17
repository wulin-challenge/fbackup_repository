package com.bjhy.fbackup.statics.agent.build;

import org.apel.gaia.container.boot.PlatformStarter;

/**
 * 静态代理启动类
 * @author 吴波
 */
public class StaticsAgentStart {
	
	public static void main(String[] args) {
		PlatformStarter.start(args);
		System.out.println("静态代理启动成功!");
	}

}
