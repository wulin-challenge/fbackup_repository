package cn.wulin.thread.expire.thread.domain;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import cn.wulin.thread.expire.thread.FixedThreadPool;

/**
 * 线程线程任务工作区
 * 线程任务工作区分为 青年区 , 中年区 ,老年区
 * 青年区: 当有新的任务需要进行执行时,首先会先进入 青年区 进行执行 , 但如果年轻区的线程已经打满 并且在最大时间内没有线程执行完毕,那么 守护清理线程会将 
 * @author wubo
 */
public class TheadTaskWork<V> {
	
	/**
	 * 青年区 : 
	 */
	private BlockingQueue<FixedThreadPool<V>> youngDistrict = new LinkedBlockingQueue<FixedThreadPool<V>>(1);
	
}
