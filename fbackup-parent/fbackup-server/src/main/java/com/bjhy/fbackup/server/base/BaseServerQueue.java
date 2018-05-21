package com.bjhy.fbackup.server.base;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;
import com.bjhy.fbackup.common.domain.ClientFileTransfer;
import com.bjhy.fbackup.common.domain.XmlFbackup;
import com.bjhy.fbackup.common.util.LoggerUtils;

/**
 * 服务端通过zookeeper的监听向对应的队列中添加客户端
 * @author wubo
 *
 */
public class BaseServerQueue {
	
	/**
	 * 队列锁
	 */
	private static ReentrantLock queueLock = new ReentrantLock();
	
	/**
	 * 客户端队列
	 */
	private static final LinkedBlockingQueue<XmlFbackup> clientQueue = new LinkedBlockingQueue<XmlFbackup>(5000);
	
	/**
	 * 客户端文件传输队列
	 */
	private static final BlockingQueue<ClientFileTransfer> ClientFileTransferQueue = new LinkedBlockingQueue<ClientFileTransfer>(5);

	/**
	 * put (clientQueue) 客户端队列
	 * @param client
	 */
	public static void putClientQueue(XmlFbackup client){
		try {
			queueLock.lock();
			if(contains(client)){
				return;
			}
			
			try {
				clientQueue.put(client);
			} catch (InterruptedException e) {
				LoggerUtils.error("在向  客户端队列  装填 (client) 时出错", e);
			}
			
		} finally {
			if(queueLock.isLocked()){
				queueLock.unlock();
			}
		}
	}
	
	/**
	 * 队列中是否已经包含该客户端
	 * @param client
	 * @return
	 */
	public static boolean contains(XmlFbackup client){
		try {
			queueLock.lock();
			String serverId = client.getServerId();
			Iterator<XmlFbackup> iterator = clientQueue.iterator();
			while(iterator.hasNext()){
				XmlFbackup alreadyClient = iterator.next();
				String serverId2 = alreadyClient.getServerId();
				if(serverId2.equals(serverId)){
					return true;
				}
			}
			return false;
		} finally {
			if(queueLock.isLocked()){
				queueLock.unlock();
			}
		}
	}

	public static BlockingQueue<XmlFbackup> getClientQueue() {
		return clientQueue;
	}
	
	public static BlockingQueue<ClientFileTransfer> getClientFileTransferQueue() {
		return ClientFileTransferQueue;
	}
	
	
//	public static void main(String[] args) throws InterruptedException {
//		XmlFbackup c1 = new XmlFbackup();
//		c1.setServerId("11");
//		
//		XmlFbackup c2 = new XmlFbackup();
//		c2.setServerId("22");
//		
//		XmlFbackup c3 = new XmlFbackup();
//		c3.setServerId("33");
//		
//		XmlFbackup c4 = new XmlFbackup();
//		c4.setServerId("44");
//		
//		XmlFbackup c5 = new XmlFbackup();
//		c5.setServerId("55");
//		
//		putClientQueue(c1);
//		putClientQueue(c2);
//		putClientQueue(c3);
//		putClientQueue(c4);
//		clientQueue.take();
//		putClientQueue(c4);
//		putClientQueue(c5);
//		
//	}
	
}
