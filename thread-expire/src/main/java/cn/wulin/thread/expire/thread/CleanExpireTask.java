package cn.wulin.thread.expire.thread;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 清除过期任务
 * @author wubo
 */
public class CleanExpireTask<V> {
	private Logger logger = LoggerFactory.getLogger(CleanExpireTask.class);
	
	private ExpireTheadManagement<V> expireTheadManagement;
	
	/**
	 * 用一个有界队列模仿一个无界队列
	 */
	private volatile BlockingQueue<FixedThreadPool<V>> expireTheadPool = new LinkedBlockingQueue<>(Integer.MAX_VALUE);
	
	/**
	 * 定时清理过期线程
	 */
	private final ScheduledThreadPoolExecutor timedCleanExpiredThread = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);

	public CleanExpireTask(ExpireTheadManagement<V> expireTheadManagement){
		this.expireTheadManagement = expireTheadManagement;
		startExpiredCleanTimed();
	}
	
	public void put(FixedThreadPool<V> fixedThreadPool){
		try {
			expireTheadPool.put(fixedThreadPool);
//			expireTheadPool.offer(fixedThreadPool, 1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logger.error("向过期队列中添加过期线程池失败!",e);
		}
	}
	
	/**
	 * 清理过期线程池
	 * @throws InterruptedException 
	 */
	private void cleanExpireTheadPool() throws InterruptedException{
		expireTheadManagement.checkFirstElementIsExpire();
		int size = expireTheadPool.size();
		for (int i = 0; i < size; i++) {
			FixedThreadPool<V> poll = expireTheadPool.poll(1, TimeUnit.SECONDS);
			if(poll != null){
				ExecutorService executor = poll.getExecutor();
				if(executor != null){
					executor.shutdownNow();
					printExpireThreadPoolLog(poll);//打印 清理的过期线程池日志
				}
			}
		}
	}
	
	/**
	 * 打印 清理的过期线程池日志
	 * @param poll
	 */
	private void printExpireThreadPoolLog(FixedThreadPool<V> poll){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date fixedThreadPoolCreateTime = poll.getFixedThreadPoolCreateTime();
		String format = sdf.format(fixedThreadPoolCreateTime);
		String timeInterval = getTimeInterval(fixedThreadPoolCreateTime, new Date());
		
		String theadName = getTheadName(poll);
		logger.warn(format+" 创建的线程池(清除的线程:"+theadName+")因为过期被成功清理!!,总存活时间:"+timeInterval+" 秒");
	}
	
	private String getTheadName(FixedThreadPool<V> poll){
		StringBuffer threadName = new StringBuffer();
		List<String> runningTheadName = poll.getRunningTheadName();
		boolean isFirst = true;
		for (String name : runningTheadName) {
			if(isFirst){
				isFirst = false;
				threadName.append(name);
			}else{
				threadName.append(","+name);
			}
		}
		return threadName.toString();
	}
	
	/**
	 * 得到时分秒的时间间隔
	 * @param start 开始时间
	 * @param end 结束时间
	 * @return
	 */
	private String getTimeInterval(Date start,Date end){
		long startLong = start.getTime();
		long endLong = end.getTime();
		
		long hh = (endLong-startLong)/(1000*60*60);
		long hh2 = (endLong-startLong)%(1000*60*60);
		
		long mm = hh2/(1000*60);
		long mm2 = hh2%(1000*60);
		
		long ss = mm2/1000;
		return hh+"时"+mm+"分"+ss+"秒";
	}

	/**
	 * 启动过期清理定时器
	 */
	private void startExpiredCleanTimed(){
		timedCleanExpiredThread.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					cleanExpireTheadPool();// 清理过期线程池
				} catch (InterruptedException e) {
					logger.error("线程被意外中断!", e);
				} 
			}
		}, 5, 30, TimeUnit.SECONDS);
	}
}
